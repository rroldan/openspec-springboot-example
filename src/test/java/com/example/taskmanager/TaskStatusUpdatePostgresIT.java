package com.example.taskmanager;

import com.example.taskmanager.adapters.out.persistence.SpringDataTaskRepository;
import com.example.taskmanager.adapters.out.persistence.TaskJpaEntity;
import com.example.taskmanager.application.port.in.UpdateTaskStatusUseCase;
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
class TaskStatusUpdatePostgresIT {

    private static final UUID TASK_ID = UUID.fromString("b0000000-0000-0000-0000-000000000002");
    private static final Instant CREATED_AT = Instant.parse("2026-01-01T12:00:00Z");

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("taskmanager")
                    .withUsername("taskmanager")
                    .withPassword("taskmanager");

    @Autowired
    private UpdateTaskStatusUseCase updateTaskStatusUseCase;

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
    void persistsStatusWithoutChangingOtherTaskFields() {
        taskRepository.save(new TaskJpaEntity(
                TASK_ID, "original title", "original description", TaskStatus.TODO, CREATED_AT, CREATED_AT));

        Task updated = updateTaskStatusUseCase.updateStatus(TASK_ID, TaskStatus.IN_PROGRESS);
        Task persisted = taskRepository.findById(TASK_ID).orElseThrow().toDomain();

        assertThat(updated.id()).isEqualTo(TASK_ID);
        assertThat(updated.title()).isEqualTo("original title");
        assertThat(updated.description()).isEqualTo("original description");
        assertThat(updated.status()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(updated.createdAt()).isEqualTo(CREATED_AT);
        assertThat(updated.updatedAt()).isAfterOrEqualTo(CREATED_AT);
        assertThat(persisted.id()).isEqualTo(updated.id());
        assertThat(persisted.title()).isEqualTo(updated.title());
        assertThat(persisted.description()).isEqualTo(updated.description());
        assertThat(persisted.status()).isEqualTo(updated.status());
        assertThat(persisted.createdAt()).isEqualTo(updated.createdAt());
        assertThat(persisted.updatedAt()).isEqualTo(
                updated.updatedAt().plusNanos(500).truncatedTo(ChronoUnit.MICROS));
    }
}
