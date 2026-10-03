package com.example.taskmanager.application.port.in;

import java.util.UUID;

public interface DeleteTaskUseCase {

    void delete(UUID id);
}
