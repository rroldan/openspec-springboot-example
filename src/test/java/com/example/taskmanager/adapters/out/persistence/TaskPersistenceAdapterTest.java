package com.example.taskmanager.adapters.out.persistence;

import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskPersistenceAdapterTest {

    private static final UUID TASK_ID = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
    private static final Instant TIMESTAMP = Instant.parse("2026-01-01T12:00:00Z");

    private final SpringDataTaskRepository repository = mock(SpringDataTaskRepository.class);
    private final TaskPersistenceAdapter adapter = new TaskPersistenceAdapter(repository);

    @Test
    void findsTaskById() {
        TaskJpaEntity entity = new TaskJpaEntity(
                TASK_ID, "title", "description", TaskStatus.TODO, TIMESTAMP, TIMESTAMP);
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(entity));

        Task task = adapter.findById(TASK_ID).orElseThrow();

        assertThat(task.id()).isEqualTo(TASK_ID);
        assertThat(task.title()).isEqualTo("title");
        verify(repository).findById(TASK_ID);
    }
}
