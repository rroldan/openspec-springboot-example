package com.example.taskmanager.adapters.in.web;

import com.example.taskmanager.domain.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Task status update request")
public record UpdateTaskStatusRequest(
        @NotNull
        @Schema(description = "Task lifecycle status", example = "IN_PROGRESS",
                requiredMode = Schema.RequiredMode.REQUIRED)
        TaskStatus status) {
}
