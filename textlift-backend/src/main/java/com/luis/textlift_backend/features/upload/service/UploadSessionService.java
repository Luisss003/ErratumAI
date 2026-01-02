package com.luis.textlift_backend.features.upload.service;

import com.luis.textlift_backend.features.auth.domain.User;
import com.luis.textlift_backend.features.document.domain.Document;
import com.luis.textlift_backend.features.document.domain.DocumentStatus;
import com.luis.textlift_backend.features.document.repository.DocumentRepository;
import com.luis.textlift_backend.features.document.service.events.DocumentQueuedEvent;
import com.luis.textlift_backend.features.upload.api.dto.*;
import com.luis.textlift_backend.features.upload.domain.UploadMode;
import com.luis.textlift_backend.features.upload.domain.UploadSession;
import com.luis.textlift_backend.features.upload.domain.UploadStatus;
import com.luis.textlift_backend.features.upload.repository.UploadSessionRepository;
import com.luis.textlift_backend.features.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UploadSessionService {

    private final UploadSessionRepository uploadRepo;
    private final DocumentRepository documentRepo;
    private final ApplicationEventPublisher events;
    private final UserRepository userRepository;
    private final VirusTotalApi virusTotalApi;

    public UploadSessionService(UploadSessionRepository uploadRepo,
                                DocumentRepository documentRepo,
                                ApplicationEventPublisher events,
                                UserRepository userRepository,
                                VirusTotalApi virusTotalApi){
        this.uploadRepo = uploadRepo;
        this.documentRepo = documentRepo;
        this.events = events;
        this.userRepository = userRepository;
        this.virusTotalApi = virusTotalApi;
    }

    public CreateUploadResponseDto createUpload(CreateUploadDto req){
        //Check business rules
        if(req.sizeBytes() > 250000000){
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File is too large...");
        }


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Could not find user!!!"
                        ));

        //First, we want to check for any existing in-progress uploads
        //of the same file that from the same user
        //(We cannot safely globally dedupe without post-processing)
        Optional<UploadSession> existingUpload =
                uploadRepo.findFirstByUser_IdAndHashAndUploadStatusIn(
                        user.getId(),
                        req.hash(),
                        List.of(UploadStatus.PENDING, UploadStatus.UPLOADING)
                );

        //If an upload by this user exists, we simply return the details of that upload
        //and tell the user to wait
        if (existingUpload.isPresent()) {
            UploadSession session = existingUpload.get();
            return new CreateUploadResponseDto(
                    UploadMode.CACHE_HIT_WAIT,
                    session.getId(),
                    session.getUploadStatus(),
                    null
            );
        }

        Optional<Document> existing = documentRepo.findByHash(req.hash());
        //If the document has been uploaded previously
        if(existing.isPresent()){
            //If the document's annotations are deemed ready
            if(existing.get().getStatus() == DocumentStatus.ANNOTATIONS_READY){
                //Then create a new upload session for this user, as to permit them to access the annotations
                //(remember that the user's ability to see annotations is based on their upload sessions)
                //and then return the CACHE_HIT to the frontend
                UploadSession session = new UploadSession();
                session.setUser(user);
                session.setUploadStatus(UploadStatus.PREMATURE_HIT);
                session.setHash(req.hash());
                uploadRepo.save(session);
                return new CreateUploadResponseDto(UploadMode.CACHE_HIT,null,null,existing.get().getId());
            }
            //Otherwise, we are in the process of generating the annotations, so ask the user to try again later
            else if(existing.get().getStatus() == DocumentStatus.ANNOTATIONS_GENERATING){
                return new CreateUploadResponseDto(UploadMode.CACHE_HIT_WAIT, null, null, null);
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Document exists but is in state: " + existing.get().getStatus());
        }else{
            //Generate an entry in the Upload table
            UploadSession session = new UploadSession();
            session.setUser(user);
            session.setUploadStatus(UploadStatus.PENDING);
            session.setHash(req.hash());

            //Add to table
            UploadSession saved = uploadRepo.save(session);
            return new CreateUploadResponseDto(UploadMode.NEW_UPLOAD,saved.getId(), saved.getUploadStatus(), null);
        }
    }

    public UploadResponseDto uploadFile(UUID uploadId, MultipartFile file){
        User user = currentUser();
        //We want to load the upload session as long as the current request is by the user that owns it
        UploadSession session = uploadRepo.findByIdAndUser_Id(uploadId, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Upload session not found"));

        if(session.getUploadStatus() != UploadStatus.PENDING){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Upload session has expired!!!");
        }

        //Update the session status
        session.setUploadStatus(UploadStatus.UPLOADING);

        //Generate a temp file path and write the directory to host
        Path dir = Path.of("/tmp/textlift/uploads/");

        //Specify .part for file uploading, then final filename
        Path finalPath = dir.resolve(uploadId + ".pdf");
        Path partPath = dir.resolve(uploadId + ".pdf.part");
        try{
            Files.createDirectories(dir);
            try(InputStream in = file.getInputStream()){
                if(file.isEmpty()){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty!!!");
                }
                if(file.getSize() > 25L * 1024 * 1024){
                    throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File is too large...");
                }

                Files.copy(in, partPath, StandardCopyOption.REPLACE_EXISTING);

                //Validate that it is a PDF file
                validatePdf(partPath);

                Files.move(partPath, finalPath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);

                //Validate hash given to us by the user with real file hash
                validateHash(finalPath, session.getHash());

                //Once the hash is validated to match the file, we can check with VirusTotal to see if it is malicious
                VirusTotalApi.Verdict virusTotalVerdict = virusTotalApi.check(session.getHash());
                if(virusTotalVerdict == VirusTotalApi.Verdict.UNSAFE){
                    //If we can't safely process the file, simply delete the upload session from the DB,
                    session.setUploadStatus(UploadStatus.REJECTED_UNSAFE);
                    uploadRepo.save(session);
                    //and below, the file itself is deleted with the catch
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "We cannot safely process this file.");
                } else if (virusTotalVerdict == VirusTotalApi.Verdict.RETRY_LATER) {
                    throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "VirusTotal rate limited; please retry shortly.");
                }

                //Update session status
                session.setUploadStatus(UploadStatus.UPLOADED);
                session.setOriginalFileName(file.getOriginalFilename());
                uploadRepo.save(session);

                return new UploadResponseDto(session.getId(), session.getUploadStatus());
            }

        } catch(IOException e){
            //Persist failure so that if user polls upload, they can see that
            //the upload failed and try again.
            session.setUploadStatus(UploadStatus.FAILED);
            uploadRepo.save(session);

            //attempt to delete partial upload from disk
            try {
                Files.deleteIfExists(partPath);
                Files.deleteIfExists(finalPath);
            } catch (IOException ignored) {}
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to store uploaded file",
                    e
            );
        } catch (ResponseStatusException e){
            if(session.getUploadStatus() != UploadStatus.REJECTED_UNSAFE) {
                session.setUploadStatus(UploadStatus.FAILED);
            }
            uploadRepo.save(session);
            try {
                Files.deleteIfExists(partPath);
                Files.deleteIfExists(finalPath);
            } catch (IOException ignored) {}
            throw e;
        }
    }

    @Transactional
    public UploadFinalizeResponseDto finalizeUpload(UUID uploadId){
        User user = currentUser();

        UploadSession session = uploadRepo.findByIdAndUser_Id(uploadId, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Upload session not found"));

        if(session.getUploadStatus() != UploadStatus.UPLOADED){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "File was not fully UPLOADED!!!");
        }

        //Do additional check to dedupe.
        Optional<Document> existing = documentRepo.findByHash(session.getHash());
        if (existing.isPresent()) {
            return new UploadFinalizeResponseDto(existing.get().getId(), existing.get().getStatus());
        }

        //Next, generate an empty document and store the file path
        Document document = new Document();
        document.setStatus(DocumentStatus.READY);
        document.setFilePath("/tmp/textlift/uploads/" + uploadId + ".pdf");
        document.setOriginalFileName(session.getOriginalFileName());
        document.setHash(session.getHash());
        documentRepo.save(document);

        //Publish finalization event so that async processor can begin extracting data immediately
        events.publishEvent(new DocumentQueuedEvent(document.getId()));

        return new UploadFinalizeResponseDto(document.getId(), document.getStatus());
    }

    public StatusResponseDto pollUploadStatus(UUID uploadId){
        User user = currentUser();

        UploadSession session = uploadRepo.findByIdAndUser_Id(uploadId, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Upload session not found"));
            return new StatusResponseDto(session.getUploadStatus());
    }

    private User currentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found!!"));
    }

    private void validatePdf(Path partPath) {
        //Checks magic bytes to validate file-type
        try (InputStream s = Files.newInputStream(partPath)) {
            byte[] head = s.readNBytes(5);
            boolean isPdf = head.length == 5
                    && head[0] == '%' && head[1] == 'P' && head[2] == 'D' && head[3] == 'F' && head[4] == '-';
            if (!isPdf){
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Not a PDF");
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to validate upload", e);
        }
    }

    private void validateHash(Path path, String userHash) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Hash algorithm unavailable", e);
        }

        try (InputStream is = Files.newInputStream(path)) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = is.read(buf)) != -1) {
                md.update(buf, 0, r);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read file for hashing", e);
        }

        String actual = HexFormat.of().formatHex(md.digest());
        if (!Objects.equals(actual, userHash)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hash mismatch");
        }
    }

}
