package com.luis.textlift_backend.features.textbook.service;

import com.luis.textlift_backend.features.document.domain.Document;
import com.luis.textlift_backend.features.document.domain.DocumentStatus;
import com.luis.textlift_backend.features.document.repository.DocumentRepository;
import com.luis.textlift_backend.features.textbook.api.dto.GoogleApiResponseDto;
import com.luis.textlift_backend.features.textbook.api.dto.TextbookLookupDto;
import com.luis.textlift_backend.features.textbook.domain.Textbook;
import com.luis.textlift_backend.features.textbook.repository.TextbookRepository;
import com.luis.textlift_backend.features.textbook.service.events.TextbookIdentifiedEvent;
import io.jsonwebtoken.lang.Arrays;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class TextbookService {
    private final TextbookRepository textbookRepository;
    private final DocumentRepository documentRepository;
    private final ApplicationEventPublisher events;
    private final GoogleBooksApi googleBooksApi;

    public TextbookService(TextbookRepository textbookRepository,
                           DocumentRepository documentRepository,
                           ApplicationEventPublisher events,
                           GoogleBooksApi googleBooksApi) {
        this.textbookRepository = textbookRepository;
        this.documentRepository = documentRepository;
        this.events = events;
        this.googleBooksApi = googleBooksApi;
    }

    @Transactional
    public void identifyTextbook(UUID documentId) {
        Document documentObj = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));


        //Extract ISBN (fail fast if you want “must-have ISBN”)
        String isbn13 = loadFrontMatterText(documentObj)
                .flatMap(IsbnExtractor::extractBestIsbn13)
                .orElseThrow(() -> {
                    documentObj.setStatus(DocumentStatus.FAILED_TO_IDENTIFY_ISBN);
                    return new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "No valid ISBN-13 found in document text"
                    );
                });

        // 2) Find-or-create textbook by ISBN (dedupe key)
        Textbook textbook = textbookRepository.findByIsbn(isbn13)
                .orElseGet(() -> {
                    Textbook created = new Textbook();
                    created.setIsbn(isbn13);
                    return textbookRepository.save(created);
                });

        // 3) Link doc -> textbook + status
        if (documentObj.getTextbook() == null || !documentObj.getTextbook().getId().equals(textbook.getId())) {
            documentObj.setTextbook(textbook);
        }
        if (documentObj.getStatus() != DocumentStatus.TEXTBOOK_IDENTIFIED) {
            documentObj.setStatus(DocumentStatus.TEXTBOOK_IDENTIFIED);
        }
        documentRepository.save(documentObj); // optional; doc is managed in txn

        //Next, given the textbook, we want to call Google Books API if the metadata fields (title, authors, edition)
        // are blank since that implies we haven't processed this before
        if(textbook.getTextbookName() == null || textbook.getTextbookName().isBlank()
                || textbook.getEdition() == null || textbook.getEdition().isBlank()
                || textbook.getAuthors() == null || textbook.getAuthors().isEmpty()){
            googleBooksApi.searchByIsbn(isbn13).ifPresent(dto -> {
                if (isBlank(textbook.getTextbookName()) && !isBlank(dto.title())) {
                    textbook.setTextbookName(dto.title());
                }
                if ((textbook.getAuthors() == null || textbook.getAuthors().isEmpty())
                        && dto.authors() != null && !dto.authors().isEmpty()) {
                    textbook.setAuthors(dto.authors());
                }
            });

            textbookRepository.save(textbook);
        }

        //Lastly, we want to only kickstart an annotation generation if this textbook hasn't already had annotation
        if(textbook.getAnnotation() == null){
            events.publishEvent(new TextbookIdentifiedEvent(textbook.getId(), documentObj.getId()));
        }
    }

    private Optional<String> loadFrontMatterText(Document doc) {
        String path = doc.getFilePath();
        if (path == null || path.isBlank()) return Optional.empty();

        int limitChars = 200_000;
        char[] buf = new char[limitChars];

        try (var reader = java.nio.file.Files.newBufferedReader(java.nio.file.Path.of(path))) {
            int read = reader.read(buf);
            if (read <= 0) return Optional.empty();
            return Optional.of(new String(buf, 0, read));
        } catch (Exception e) {
            return Optional.empty();
        }
    }



}
