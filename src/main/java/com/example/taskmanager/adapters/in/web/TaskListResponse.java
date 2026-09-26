package com.example.taskmanager.adapters.in.web;

import com.example.taskmanager.application.model.TaskPage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paged task listing response")
public record TaskListResponse(
        @Schema(description = "Task items on this page") List<TaskResponse> items,
        @Schema(example = "0") int page,
        @Schema(example = "20") int size,
        @Schema(example = "150") long totalElements,
        @Schema(example = "8") int totalPages) {

    public static TaskListResponse from(TaskPage page) {
        return new TaskListResponse(
                page.items().stream().map(TaskResponse::from).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }
}
