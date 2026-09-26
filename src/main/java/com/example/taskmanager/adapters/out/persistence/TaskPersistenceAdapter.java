package com.example.taskmanager.adapters.out.persistence;

import com.example.taskmanager.application.model.TaskPage;
import com.example.taskmanager.application.model.TaskSearchCriteria;
import com.example.taskmanager.application.model.TaskSort;
import com.example.taskmanager.application.port.out.TaskQueryRepository;
import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.domain.model.Task;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskPersistenceAdapter implements TaskRepository, TaskQueryRepository {

    private static final char LIKE_ESCAPE = '\\';

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

    @Override
    public TaskPage findAll(TaskSearchCriteria criteria) {
        Page<TaskJpaEntity> page = repository.findAll(toSpecification(criteria), toPageable(criteria));
        return new TaskPage(
                page.map(TaskJpaEntity::toDomain).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    private static Specification<TaskJpaEntity> toSpecification(TaskSearchCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria.status() != null) {
                predicates.add(builder.equal(root.get("status"), criteria.status()));
            }
            if (criteria.query() != null && !criteria.query().isEmpty()) {
                String pattern = "%" + escapeLike(criteria.query().toLowerCase(Locale.ROOT)) + "%";
                Predicate titleMatch = builder.like(
                        builder.lower(root.get("title")), pattern, LIKE_ESCAPE);
                Predicate descriptionMatch = builder.like(
                        builder.lower(root.get("description")), pattern, LIKE_ESCAPE);
                predicates.add(builder.or(titleMatch, descriptionMatch));
            }
            return predicates.isEmpty()
                    ? builder.conjunction()
                    : builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static PageRequest toPageable(TaskSearchCriteria criteria) {
        TaskSort taskSort = criteria.sort() == null
                ? new TaskSort(TaskSort.Field.CREATED_AT, TaskSort.Direction.DESC)
                : criteria.sort();
        String property = switch (taskSort.field()) {
            case CREATED_AT -> "createdAt";
            case TITLE -> "title";
        };
        Sort.Direction direction = switch (taskSort.direction()) {
            case ASC -> Sort.Direction.ASC;
            case DESC -> Sort.Direction.DESC;
        };
        return PageRequest.of(
                criteria.page(),
                criteria.size(),
                Sort.by(new Sort.Order(direction, property), Sort.Order.asc("id")));
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
