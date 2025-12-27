package com.luis.textlift_backend.features.textbook.repository;

import com.luis.textlift_backend.features.textbook.domain.Textbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TextbookRepository extends JpaRepository<Textbook, UUID> {
    Optional<Textbook> findByTextbookName(String s);

    Optional<Textbook> findByIsbn(String isbn);
}
