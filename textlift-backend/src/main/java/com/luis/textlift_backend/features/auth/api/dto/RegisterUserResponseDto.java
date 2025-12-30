package com.luis.textlift_backend.features.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegisterUserResponseDto(
        @NotNull UUID userId,
        @NotBlank String email,
        @NotBlank String fullName
){}
