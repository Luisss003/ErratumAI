package com.luis.textlift_backend.features.annotation.repository;

import com.luis.textlift_backend.features.annotation.domain.AnnotationNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnnotationNoteRepository extends JpaRepository<AnnotationNote, UUID> {
}
