package com.example.taskmanager;

import com.example.taskmanager.adapters.out.persistence.SpringDataTaskRepository;
import com.example.taskmanager.adapters.out.persistence.TaskJpaEntity;
import com.example.taskmanager.application.port.in.UpdateTaskUseCase;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class TaskUpdatePostgresIT {

    private static final UUID TASK_ID = UUID.fromString("b0000000-0000-0000-0000-000000000001");
    private static final Instant CREATED_AT = Instant.parse("2026-01-01T12:00:00Z");

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("taskmanager")
                    .withUsername("taskmanager")
                    .withPassword("taskmanager");

    @Autowired
    private UpdateTaskUseCase updateTaskUseCase;

    @Autowired
    private SpringDataTaskRepository taskRepository;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @AfterEach
    void removeFixture() {
        taskRepository.deleteById(TASK_ID);
    }

    @Test
    void persistsReplacementWithoutChangingIdOrCreationTime() {
        taskRepository.save(new TaskJpaEntity(
                TASK_ID, "original title", "original description", TaskStatus.TODO, CREATED_AT, CREATED_AT));
        Instant persistedCreatedAt = taskRepository.findById(TASK_ID).orElseThrow()
                .toDomain().createdAt();

        Task updated = updateTaskUseCase.update(
                TASK_ID, "updated title", "updated description", TaskStatus.IN_PROGRESS);
        Task persisted = taskRepository.findById(TASK_ID).orElseThrow().toDomain();

        assertThat(updated.id()).isEqualTo(TASK_ID);
        assertThat(updated.title()).isEqualTo("updated title");
        assertThat(updated.description()).isEqualTo("updated description");
        assertThat(updated.status()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(updated.createdAt()).isEqualTo(persistedCreatedAt);
        assertThat(updated.updatedAt()).isAfterOrEqualTo(persistedCreatedAt);
        assertThat(persisted.id()).isEqualTo(TASK_ID);
        assertThat(persisted.title()).isEqualTo("updated title");
        assertThat(persisted.description()).isEqualTo("updated description");
        assertThat(persisted.status()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(persisted.createdAt()).isEqualTo(persistedCreatedAt);
        assertThat(persisted.updatedAt()).isEqualTo(updated.updatedAt().plusNanos(500)
                .truncatedTo(ChronoUnit.MICROS));
    }
}
