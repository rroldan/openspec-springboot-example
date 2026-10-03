package com.example.taskmanager.application.port.in;

import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;

import java.util.UUID;

public interface UpdateTaskStatusUseCase {

    Task updateStatus(UUID id, TaskStatus status);
}
