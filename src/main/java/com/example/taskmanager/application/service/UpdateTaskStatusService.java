package com.example.taskmanager.application.service;

import com.example.taskmanager.application.port.in.UpdateTaskStatusUseCase;
import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class UpdateTaskStatusService implements UpdateTaskStatusUseCase {

    private final TaskRepository taskRepository;
    private final Clock clock;

    public UpdateTaskStatusService(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    @Override
    public Task updateStatus(UUID id, TaskStatus status) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        Instant now = Instant.now(clock);
        return taskRepository.save(new Task(
                existing.id(),
                existing.title(),
                existing.description(),
                status,
                existing.createdAt(),
                now));
    }
}
