package com.luis.textlift_backend.features.textbook.service.events;

import java.util.UUID;

public record TextbookIdentifiedEvent (UUID textbookId, UUID documentId){ }

