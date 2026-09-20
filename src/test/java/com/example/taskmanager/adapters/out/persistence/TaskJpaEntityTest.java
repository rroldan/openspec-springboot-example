package com.example.taskmanager.adapters.out.persistence;

import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskJpaEntityTest {

    @Test
    void mapsAllTaskFieldsToAndFromEntity() {
        Task task = new Task(
                UUID.randomUUID(),
                "title",
                "description",
                TaskStatus.IN_PROGRESS,
                Instant.parse("2026-01-01T12:00:00Z"),
                Instant.parse("2026-01-01T12:01:00Z"));

        Task mapped = TaskJpaEntity.fromDomain(task).toDomain();

        assertThat(mapped).isEqualTo(task);
    }
}
