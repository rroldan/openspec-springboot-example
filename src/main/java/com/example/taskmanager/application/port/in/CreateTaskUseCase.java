package com.example.taskmanager.application.port.in;

import com.example.taskmanager.domain.model.Task;

public interface CreateTaskUseCase {

    Task create(String title, String description);
}
