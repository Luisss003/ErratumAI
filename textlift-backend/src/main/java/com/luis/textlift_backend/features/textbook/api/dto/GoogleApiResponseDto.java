package com.luis.textlift_backend.features.textbook.api.dto;

public record GoogleApiResponseDto (
        String title,
        String[] authors
){}
