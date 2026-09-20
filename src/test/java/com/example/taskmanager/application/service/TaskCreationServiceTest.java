package com.example.taskmanager.application.service;

import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskCreationServiceTest {

    private final Instant now = Instant.parse("2026-01-01T12:00:00Z");
    private final TaskRepository repository = mock(TaskRepository.class);
    private final TaskCreationService service = new TaskCreationService(
            repository, Clock.fixed(now, ZoneOffset.UTC));

    @Test
    void createsTodoTaskWithServerTimestamps() {
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task task = service.create("title", "description");

        assertThat(task.title()).isEqualTo("title");
        assertThat(task.description()).isEqualTo("description");
        assertThat(task.status()).isEqualTo(TaskStatus.TODO);
        assertThat(task.createdAt()).isEqualTo(now);
        assertThat(task.updatedAt()).isEqualTo(now);
        verify(repository).save(any(Task.class));
    }
}
