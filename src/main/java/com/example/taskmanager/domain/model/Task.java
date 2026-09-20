package com.example.taskmanager.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Task(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public Task {
        Objects.requireNonNull(id, "id must not be null");
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }
}
