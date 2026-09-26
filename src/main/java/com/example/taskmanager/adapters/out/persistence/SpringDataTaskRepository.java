package com.example.taskmanager.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataTaskRepository extends JpaRepository<TaskJpaEntity, UUID>,
        JpaSpecificationExecutor<TaskJpaEntity> {
}
