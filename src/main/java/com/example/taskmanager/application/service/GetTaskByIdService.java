package com.example.taskmanager.application.service;

import com.example.taskmanager.application.port.in.GetTaskByIdUseCase;
import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.domain.model.Task;

import java.util.UUID;

public class GetTaskByIdService implements GetTaskByIdUseCase {

    private final TaskRepository taskRepository;

    public GetTaskByIdService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task getById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
