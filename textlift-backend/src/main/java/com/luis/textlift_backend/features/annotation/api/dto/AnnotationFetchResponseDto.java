package com.luis.textlift_backend.features.annotation.api.dto;

import com.luis.textlift_backend.features.annotation.domain.AnnotationNote;

import java.util.List;

public record AnnotationFetchResponseDto(
        List<AnnotationNote> notes
){}

