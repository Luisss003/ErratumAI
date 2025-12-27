package com.luis.textlift_backend.features.upload.api.dto;

import jakarta.validation.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record CreateUploadDto(
        @NotBlank
        @Size(min = 64, max = 64, message = "must be sha256 hash")
        String hash,

        @NotNull
        @Min(1) @Max(500000000)
        Long sizeBytes
){}
