package com.example.taskmanager.adapters.out.persistence;

import com.example.taskmanager.application.model.TaskPage;
import com.example.taskmanager.application.model.TaskSearchCriteria;
import com.example.taskmanager.application.model.TaskSort;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskQueryPersistenceAdapterTest {

    private static final Instant TIMESTAMP = Instant.parse("2026-01-01T12:00:00Z");

    private final SpringDataTaskRepository repository = mock(SpringDataTaskRepository.class);
    private final TaskPersistenceAdapter adapter = new TaskPersistenceAdapter(repository);

    @Test
    void combinesStatusAndLiteralSearchAndMapsRequestedPage() {
        TaskJpaEntity entity = entity("matching task");
        when(repository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(entity), PageRequest.of(2, 7), 23));

        TaskPage result = adapter.findAll(new TaskSearchCriteria(
                TaskStatus.DONE,
                "a%_\\b",
                2,
                7,
                new TaskSort(TaskSort.Field.TITLE, TaskSort.Direction.DESC)));

        assertThat(result.items()).containsExactly(entity.toDomain());
        assertThat(result.page()).isEqualTo(2);
        assertThat(result.size()).isEqualTo(7);
        assertThat(result.totalElements()).isEqualTo(23);
        assertThat(result.totalPages()).isEqualTo(4);

        var specificationCaptor = org.mockito.ArgumentCaptor.forClass(Specification.class);
        var pageableCaptor = org.mockito.ArgumentCaptor.forClass(PageRequest.class);
        verify(repository).findAll(specificationCaptor.capture(), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(2);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(7);
        assertThat(pageableCaptor.getValue().getSort().toList()).containsExactly(
                Sort.Order.desc("title"),
                Sort.Order.asc("id"));

        assertCombinedPredicates(specificationCaptor.getValue());
    }

    @Test
    void sortsCreatedAtWithIdTieBreakerAndMapsEmptyPageMetadata() {
        when(repository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        TaskPage result = adapter.findAll(new TaskSearchCriteria(
                null,
                null,
                0,
                20,
                new TaskSort(TaskSort.Field.CREATED_AT, TaskSort.Direction.DESC)));

        assertThat(result.items()).isEmpty();
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(20);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();

        var pageableCaptor = org.mockito.ArgumentCaptor.forClass(PageRequest.class);
        verify(repository).findAll(any(Specification.class), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort().toList()).containsExactly(
                Sort.Order.desc("createdAt"),
                Sort.Order.asc("id"));
    }

    @Test
    void sortsTitleAscendingWithIdTieBreaker() {
        when(repository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 4), 0));

        adapter.findAll(new TaskSearchCriteria(
                null,
                null,
                1,
                4,
                new TaskSort(TaskSort.Field.TITLE, TaskSort.Direction.ASC)));

        var pageableCaptor = org.mockito.ArgumentCaptor.forClass(PageRequest.class);
        verify(repository).findAll(any(Specification.class), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort().toList()).containsExactly(
                Sort.Order.asc("title"),
                Sort.Order.asc("id"));
    }

    private static void assertCombinedPredicates(Specification<TaskJpaEntity> specification) {
        Root<TaskJpaEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Path<TaskStatus> statusPath = mock(Path.class);
        Path<String> titlePath = mock(Path.class);
        Path<String> descriptionPath = mock(Path.class);
        Expression<String> lowercaseTitle = mock(Expression.class);
        Expression<String> lowercaseDescription = mock(Expression.class);
        Predicate statusPredicate = mock(Predicate.class);
        Predicate titlePredicate = mock(Predicate.class);
        Predicate descriptionPredicate = mock(Predicate.class);
        Predicate searchPredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        when(root.<TaskStatus>get("status")).thenReturn(statusPath);
        when(root.<String>get("title")).thenReturn(titlePath);
        when(root.<String>get("description")).thenReturn(descriptionPath);
        when(criteriaBuilder.equal(statusPath, TaskStatus.DONE)).thenReturn(statusPredicate);
        when(criteriaBuilder.lower(titlePath)).thenReturn(lowercaseTitle);
        when(criteriaBuilder.lower(descriptionPath)).thenReturn(lowercaseDescription);
        when(criteriaBuilder.like(lowercaseTitle, "%a\\%\\_\\\\b%", '\\')).thenReturn(titlePredicate);
        when(criteriaBuilder.like(lowercaseDescription, "%a\\%\\_\\\\b%", '\\'))
                .thenReturn(descriptionPredicate);
        when(criteriaBuilder.or(titlePredicate, descriptionPredicate)).thenReturn(searchPredicate);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(combinedPredicate);

        assertThat(specification.toPredicate(root, query, criteriaBuilder)).isSameAs(combinedPredicate);
        verify(criteriaBuilder).equal(statusPath, TaskStatus.DONE);
        verify(criteriaBuilder).like(lowercaseTitle, "%a\\%\\_\\\\b%", '\\');
        verify(criteriaBuilder).like(lowercaseDescription, "%a\\%\\_\\\\b%", '\\');
        verify(criteriaBuilder).or(titlePredicate, descriptionPredicate);
        var predicatesCaptor = org.mockito.ArgumentCaptor.forClass(Predicate[].class);
        verify(criteriaBuilder).and(predicatesCaptor.capture());
        assertThat(predicatesCaptor.getValue()).containsExactly(statusPredicate, searchPredicate);
    }

    private static TaskJpaEntity entity(String title) {
        return new TaskJpaEntity(
                UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f"),
                title,
                "description",
                TaskStatus.DONE,
                TIMESTAMP,
                TIMESTAMP);
    }
}
