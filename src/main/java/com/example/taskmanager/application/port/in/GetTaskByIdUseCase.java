package com.example.taskmanager.application.port.in;

import com.example.taskmanager.domain.model.Task;

import java.util.UUID;

public interface GetTaskByIdUseCase {

    Task getById(UUID id);
}
