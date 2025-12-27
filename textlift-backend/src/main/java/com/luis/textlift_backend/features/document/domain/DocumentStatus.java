package com.luis.textlift_backend.features.document.domain;

public enum DocumentStatus {
    READY,
    SCANNING,
    TEXTBOOK_SCANNING,
    TEXTBOOK_IDENTIFIED,
    ANNOTATIONS_GENERATING,
    ANNOTATIONS_READY,
    FAILED_TO_GENERATE,
    FAILED_TO_IDENTIFY_ISBN,
}
