package com.example.taskmanager.application.service;

import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateTaskStatusServiceTest {

    private static final UUID TASK_ID = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
    private static final Instant CREATED_AT = Instant.parse("2026-01-01T12:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-01-02T12:00:00Z");

    private final TaskRepository repository = mock(TaskRepository.class);
    private final UpdateTaskStatusService service = new UpdateTaskStatusService(
            repository, Clock.fixed(UPDATED_AT, ZoneOffset.UTC));

    @Test
    void updatesOnlyStatusAndPreservesOtherTaskFields() {
        Task existing = new Task(
                TASK_ID, "original title", "original description", TaskStatus.TODO, CREATED_AT, CREATED_AT);
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(existing));
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updated = service.updateStatus(TASK_ID, TaskStatus.IN_PROGRESS);

        assertThat(updated).isEqualTo(new Task(
                TASK_ID, "original title", "original description", TaskStatus.IN_PROGRESS, CREATED_AT, UPDATED_AT));
        verify(repository).save(updated);
    }

    @Test
    void raisesNotFoundWhenTaskDoesNotExist() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(TASK_ID, TaskStatus.DONE))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task with id " + TASK_ID + " was not found");
    }
}
