package com.example.taskmanager.application.service;

import com.example.taskmanager.application.port.in.CreateTaskUseCase;
import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class TaskCreationService implements CreateTaskUseCase {

    private final TaskRepository taskRepository;
    private final Clock clock;

    public TaskCreationService(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    @Override
    public Task create(String title, String description) {
        Instant now = Instant.now(clock);
        return taskRepository.save(new Task(
                UUID.randomUUID(),
                title,
                description,
                TaskStatus.TODO,
                now,
                now));
    }
}
