package com.luis.textlift_backend.features.upload.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "upload_session"
)
public class UploadSession {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false) @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus;

    @Column
    private String originalFileName;

    @Column
    private String hash;

    public void setUploadStatus(UploadStatus status){
        this.uploadStatus = status;
    }
    public UUID getId(){
        return this.id;
    }
    public UploadStatus getUploadStatus(){
        return this.uploadStatus;
    }
    public String getOriginalFileName() {
        return originalFileName;
    }
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    public String getMd5() {
        return this.hash;
    }
    public void setMd5(String md5) {
        this.hash = md5;
    }
}
