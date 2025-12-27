package com.luis.textlift_backend.features.upload.repository;

import com.luis.textlift_backend.features.upload.domain.UploadSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UploadSessionRepository extends JpaRepository<UploadSession, UUID> {
}
