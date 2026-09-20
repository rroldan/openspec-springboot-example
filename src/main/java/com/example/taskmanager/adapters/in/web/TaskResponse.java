package com.example.taskmanager.adapters.in.web;

import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Persisted task")
public record TaskResponse(
        @Schema(example = "3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f") UUID id,
        @Schema(example = "Prepare release notes") String title,
        @Schema(example = "Summarize the changes for the next release") String description,
        @Schema(example = "TODO") TaskStatus status,
        @Schema(example = "2026-01-01T12:00:00Z") Instant createdAt,
        @Schema(example = "2026-01-01T12:00:00Z") Instant updatedAt) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(task.id(), task.title(), task.description(), task.status(),
                task.createdAt(), task.updatedAt());
    }
}
