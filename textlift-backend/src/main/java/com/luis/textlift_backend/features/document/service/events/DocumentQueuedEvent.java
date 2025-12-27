package com.luis.textlift_backend.features.document.service.events;

import java.util.UUID;

//Event looks out for document ID so knows which doc to process
public record DocumentQueuedEvent(UUID documentId) {}

