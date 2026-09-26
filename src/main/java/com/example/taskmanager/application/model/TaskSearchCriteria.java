package com.example.taskmanager.application.model;

import com.example.taskmanager.domain.model.TaskStatus;

public record TaskSearchCriteria(
        TaskStatus status,
        String query,
        int page,
        int size,
        TaskSort sort) {
}
