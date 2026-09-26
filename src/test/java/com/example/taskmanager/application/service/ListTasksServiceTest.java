package com.example.taskmanager.application.service;

import com.example.taskmanager.application.model.TaskPage;
import com.example.taskmanager.application.model.TaskSearchCriteria;
import com.example.taskmanager.application.model.TaskSort;
import com.example.taskmanager.application.port.in.ListTasksUseCase;
import com.example.taskmanager.application.port.out.TaskQueryRepository;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListTasksServiceTest {

    private static final Task TASK = new Task(
            UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f"),
            "title",
            "description",
            TaskStatus.TODO,
            Instant.parse("2026-01-01T12:00:00Z"),
            Instant.parse("2026-01-01T12:00:00Z"));

    @Test
    void appliesDefaultCriteriaAndReturnsPageMetadata() {
        TaskPage expectedPage = new TaskPage(List.of(TASK), 0, 20, 1, 1);
        CapturingTaskQueryRepository queryRepository = new CapturingTaskQueryRepository(expectedPage);
        ListTasksUseCase service = new ListTasksService(queryRepository);

        TaskPage result = service.list(null);

        assertThat(queryRepository.receivedCriteria).isEqualTo(new TaskSearchCriteria(
                null,
                null,
                0,
                20,
                new TaskSort(TaskSort.Field.CREATED_AT, TaskSort.Direction.DESC)));
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.items()).containsExactly(TASK);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(20);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void passesRequestedFiltersPaginationAndSortAndReturnsPageMetadata() {
        TaskPage expectedPage = new TaskPage(List.of(TASK), 2, 5, 11, 3);
        CapturingTaskQueryRepository queryRepository = new CapturingTaskQueryRepository(expectedPage);
        ListTasksUseCase service = new ListTasksService(queryRepository);

        TaskPage result = service.list(new TaskSearchCriteria(
                TaskStatus.DONE,
                "release",
                2,
                5,
                new TaskSort(TaskSort.Field.TITLE, null)));

        assertThat(queryRepository.receivedCriteria).isEqualTo(new TaskSearchCriteria(
                TaskStatus.DONE,
                "release",
                2,
                5,
                new TaskSort(TaskSort.Field.TITLE, TaskSort.Direction.ASC)));
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.page()).isEqualTo(2);
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.totalElements()).isEqualTo(11);
        assertThat(result.totalPages()).isEqualTo(3);
    }

    @Test
    void treatsEmptySearchTextAsNoTextFilter() {
        CapturingTaskQueryRepository queryRepository = new CapturingTaskQueryRepository(
                new TaskPage(List.of(), 0, 20, 0, 0));
        ListTasksUseCase service = new ListTasksService(queryRepository);

        service.list(new TaskSearchCriteria(null, "", 0, 20, null));

        assertThat(queryRepository.receivedCriteria.query()).isNull();
    }

    private static final class CapturingTaskQueryRepository implements TaskQueryRepository {

        private final TaskPage result;
        private TaskSearchCriteria receivedCriteria;

        private CapturingTaskQueryRepository(TaskPage result) {
            this.result = result;
        }

        @Override
        public TaskPage findAll(TaskSearchCriteria criteria) {
            receivedCriteria = criteria;
            return result;
        }
    }
}
