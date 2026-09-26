package com.example.taskmanager.application.model;

import java.util.Objects;

public record TaskSort(Field field, Direction direction) {

    public TaskSort {
        Objects.requireNonNull(field, "field must not be null");
        Objects.requireNonNull(direction, "direction must not be null");
    }

    public enum Field {
        CREATED_AT,
        TITLE
    }

    public enum Direction {
        ASC,
        DESC
    }
}
