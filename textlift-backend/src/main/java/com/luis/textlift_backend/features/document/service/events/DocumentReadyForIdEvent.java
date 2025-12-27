package com.luis.textlift_backend.features.document.service.events;

import com.luis.textlift_backend.features.document.domain.ExtractedMetadata;

import java.util.UUID;

public record DocumentReadyForIdEvent(ExtractedMetadata metadata, UUID documentId) { }

