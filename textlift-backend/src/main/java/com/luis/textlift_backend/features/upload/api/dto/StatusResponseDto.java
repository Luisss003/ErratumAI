package com.luis.textlift_backend.features.upload.api.dto;

import com.luis.textlift_backend.features.upload.domain.UploadStatus;
import jakarta.validation.constraints.NotNull;

public record StatusResponseDto(
        @NotNull
        UploadStatus status
) {
}
