package com.example.taskmanager.adapters.out.persistence;

import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.domain.model.Task;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskPersistenceAdapter implements TaskRepository {

    private final SpringDataTaskRepository repository;

    public TaskPersistenceAdapter(SpringDataTaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Task save(Task task) {
        return repository.save(TaskJpaEntity.fromDomain(task)).toDomain();
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return repository.findById(id).map(TaskJpaEntity::toDomain);
    }
}
