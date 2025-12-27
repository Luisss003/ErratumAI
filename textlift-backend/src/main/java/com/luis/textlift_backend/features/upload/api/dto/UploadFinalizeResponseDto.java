package com.luis.textlift_backend.features.upload.api.dto;

import com.luis.textlift_backend.features.document.domain.DocumentStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UploadFinalizeResponseDto(
        @NotNull
        UUID documentId,

        @NotNull
        DocumentStatus status
){}
