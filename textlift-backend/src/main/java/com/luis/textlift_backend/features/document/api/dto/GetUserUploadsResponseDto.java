package com.luis.textlift_backend.features.document.api.dto;

import com.luis.textlift_backend.features.document.domain.DocumentStatus;

import java.util.UUID;

public record GetUserUploadsResponseDto (

        String textBookTitle,
        DocumentStatus documentStatus,
        UUID documentId
){}
