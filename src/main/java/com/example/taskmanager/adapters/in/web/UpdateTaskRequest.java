package com.example.taskmanager.adapters.in.web;

import com.example.taskmanager.domain.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Task replacement request")
public record UpdateTaskRequest(
        @NotBlank
        @Schema(description = "Task title", example = "Prepare release notes", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        @NotNull
        @Schema(description = "Task description", example = "Summarize the changes for the next release", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
        @NotNull
        @Schema(description = "Task lifecycle status", example = "IN_PROGRESS", requiredMode = Schema.RequiredMode.REQUIRED)
        TaskStatus status) {
}
