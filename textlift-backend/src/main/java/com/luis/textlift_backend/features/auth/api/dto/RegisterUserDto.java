package com.luis.textlift_backend.features.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record RegisterUserDto (
        @NotBlank @Email
        String email,

        @NotBlank
        String password,

        @NotEmpty
        String fullName
){}
