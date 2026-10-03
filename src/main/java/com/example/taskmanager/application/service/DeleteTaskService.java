package com.example.taskmanager.application.service;

import com.example.taskmanager.application.port.in.DeleteTaskUseCase;
import com.example.taskmanager.application.port.out.TaskRepository;

import java.util.UUID;

public class DeleteTaskService implements DeleteTaskUseCase {

    private final TaskRepository taskRepository;

    public DeleteTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void delete(UUID id) {
        taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.deleteById(id);
    }
}
