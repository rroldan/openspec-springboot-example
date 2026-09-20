package com.example.taskmanager.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataTaskRepository extends JpaRepository<TaskJpaEntity, UUID> {
}
