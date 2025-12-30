package com.luis.textlift_backend.features.textbook.api.dto;

import java.util.List;

public record TextbookLookupDto(
        String googleVolumeId,
        String title,
        List<String> authors,
        String publisher,
        String publishedDate,
        Integer pageCount,
        String thumbnail,
        String description,
        String textSnippet
) {}
