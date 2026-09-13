package com.example.taskmanager.adapters.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standard error returned by the REST API")
public record ApiError(
        @Schema(example = "METHOD_NOT_ALLOWED") String errorId,
        @Schema(example = "HTTP method is not supported for this resource") String message,
        @Schema(example = "2026-01-01T12:00:00Z") Instant timestamp,
        @Schema(example = "3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f") String correlationId) {
}
