package com.luis.textlift_backend.features.annotation.api;

import com.luis.textlift_backend.features.annotation.api.dto.AnnotationFetchResponseDto;
import com.luis.textlift_backend.features.annotation.service.AnnotationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/annotation")
public class AnnotationController {
    private final AnnotationService annotationService;
    public AnnotationController(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    @GetMapping("/{id}")
    public AnnotationFetchResponseDto getAnnotationsByDocId(@PathVariable UUID id){
        return this.annotationService.getNotesByDocId(id);
    }
}