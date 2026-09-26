package com.example.taskmanager.application.model;

import com.example.taskmanager.domain.model.Task;

import java.util.List;
import java.util.Objects;

public record TaskPage(
        List<Task> items,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public TaskPage {
        items = List.copyOf(Objects.requireNonNull(items, "items must not be null"));
    }
}
