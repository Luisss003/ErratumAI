package com.luis.textlift_backend.features.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserDto (
        @NotBlank @Email
        String email,

        @NotBlank
        String password
){}
