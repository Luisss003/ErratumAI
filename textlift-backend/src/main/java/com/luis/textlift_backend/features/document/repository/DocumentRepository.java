package com.luis.textlift_backend.features.document.repository;

import com.luis.textlift_backend.features.document.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Optional<Document> findByHash(String s);
}
