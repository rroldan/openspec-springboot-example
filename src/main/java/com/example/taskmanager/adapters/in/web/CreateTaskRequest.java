package com.example.taskmanager.adapters.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Task creation request")
public record CreateTaskRequest(
        @NotBlank
        @Schema(description = "Task title", example = "Prepare release notes", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        @NotNull
        @Schema(description = "Task description", example = "Summarize the changes for the next release", requiredMode = Schema.RequiredMode.REQUIRED)
        String description) {
}
