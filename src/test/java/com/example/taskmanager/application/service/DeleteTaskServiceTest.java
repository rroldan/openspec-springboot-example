package com.example.taskmanager.application.service;

import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DeleteTaskServiceTest {

    private static final UUID TASK_ID = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
    private static final Task TASK = new Task(
            TASK_ID,
            "title",
            "description",
            TaskStatus.TODO,
            Instant.parse("2026-01-01T12:00:00Z"),
            Instant.parse("2026-01-01T12:00:00Z"));

    private final TaskRepository repository = mock(TaskRepository.class);
    private final DeleteTaskService service = new DeleteTaskService(repository);

    @Test
    void deletesExistingTask() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(TASK));

        service.delete(TASK_ID);

        verify(repository).findById(TASK_ID);
        verify(repository).deleteById(TASK_ID);
    }

    @Test
    void raisesNotFoundWithoutDeletingWhenTaskDoesNotExist() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(TASK_ID))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task with id " + TASK_ID + " was not found");

        verify(repository).findById(TASK_ID);
        verifyNoMoreInteractions(repository);
    }
}
