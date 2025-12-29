package com.luis.textlift_backend.features.annotation.repository;

import com.luis.textlift_backend.features.annotation.domain.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnnotationRepository extends JpaRepository<Annotation, UUID> {
}
