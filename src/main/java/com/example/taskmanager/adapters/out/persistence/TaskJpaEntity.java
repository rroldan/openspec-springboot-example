package com.example.taskmanager.adapters.out.persistence;

import com.example.taskmanager.domain.model.TaskStatus;
import com.example.taskmanager.domain.model.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class TaskJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TaskJpaEntity() {
    }

    public TaskJpaEntity(UUID id, String title, String description, TaskStatus status,
                         Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TaskJpaEntity fromDomain(Task task) {
        return new TaskJpaEntity(task.id(), task.title(), task.description(), task.status(),
                task.createdAt(), task.updatedAt());
    }

    public Task toDomain() {
        return new Task(
                id, title, description, status, createdAt, updatedAt);
    }
}
