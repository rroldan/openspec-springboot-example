package com.example.taskmanager.application.port.out;

import com.example.taskmanager.domain.model.Task;

public interface TaskRepository {

    Task save(Task task);
}
