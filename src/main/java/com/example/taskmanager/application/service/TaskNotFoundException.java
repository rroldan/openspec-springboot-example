package com.example.taskmanager.application.service;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(UUID id) {
        super("Task with id " + id + " was not found");
    }
}
