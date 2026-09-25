package com.example.taskmanager.application.port.out;

import com.example.taskmanager.domain.model.Task;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(UUID id);
}
