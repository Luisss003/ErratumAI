package com.luis.textlift_backend.features.upload.api.dto;

import com.luis.textlift_backend.features.upload.domain.UploadStatus;

import java.util.UUID;

public record UploadResponseDto(
        UUID uploadId,
        UploadStatus status
){ }

