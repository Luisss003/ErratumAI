package com.luis.textlift_backend.features.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginUserResponseDto (
        @NotBlank
        String token,

        @NotNull
        Long expiresIn
){}
