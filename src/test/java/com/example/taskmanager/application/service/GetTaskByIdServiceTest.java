package com.example.taskmanager.application.service;

import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetTaskByIdServiceTest {

    private static final UUID TASK_ID = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
    private static final Task TASK = new Task(
            TASK_ID,
            "title",
            "description",
            TaskStatus.TODO,
            Instant.parse("2026-01-01T12:00:00Z"),
            Instant.parse("2026-01-01T12:00:00Z"));

    private final TaskRepository repository = mock(TaskRepository.class);
    private final GetTaskByIdService service = new GetTaskByIdService(repository);

    @Test
    void returnsExistingTask() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(TASK));

        assertThat(service.getById(TASK_ID)).isEqualTo(TASK);
        verify(repository).findById(TASK_ID);
    }

    @Test
    void raisesNotFoundWhenTaskDoesNotExist() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(TASK_ID))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task with id " + TASK_ID + " was not found");
    }
}
