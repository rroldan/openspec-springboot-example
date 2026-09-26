package com.example.taskmanager.application.port;

import com.example.taskmanager.application.port.in.ListTasksUseCase;
import com.example.taskmanager.application.port.out.TaskQueryRepository;
import com.example.taskmanager.application.model.TaskPage;
import com.example.taskmanager.application.model.TaskSearchCriteria;
import com.example.taskmanager.application.model.TaskSort;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskListApplicationContractTest {

    @Test
    void modelsAndPortsRepresentAFrameworkIndependentTaskQuery() {
        Task task = new Task(
                UUID.randomUUID(),
                "Release",
                "Prepare release",
                TaskStatus.DONE,
                Instant.parse("2026-01-01T12:00:00Z"),
                Instant.parse("2026-01-01T12:00:00Z"));
        TaskSearchCriteria criteria = new TaskSearchCriteria(
                TaskStatus.DONE,
                "release",
                1,
                10,
                new TaskSort(TaskSort.Field.TITLE, TaskSort.Direction.DESC));
        TaskPage page = new TaskPage(List.of(task), 1, 10, 11, 2);

        assertThat(criteria.status()).isEqualTo(TaskStatus.DONE);
        assertThat(criteria.query()).isEqualTo("release");
        assertThat(criteria.page()).isEqualTo(1);
        assertThat(criteria.size()).isEqualTo(10);
        assertThat(criteria.sort().field()).isEqualTo(TaskSort.Field.TITLE);
        assertThat(criteria.sort().direction()).isEqualTo(TaskSort.Direction.DESC);
        assertThat(page.items()).containsExactly(task);
        assertThat(page.totalElements()).isEqualTo(11);
        assertThat(page.page()).isEqualTo(1);
        assertThat(page.size()).isEqualTo(10);
        assertThat(page.totalPages()).isEqualTo(2);

        assertThat(ListTasksUseCase.class.getPackageName()).startsWith("com.example.taskmanager.application");
        assertThat(TaskQueryRepository.class.getPackageName()).startsWith("com.example.taskmanager.application");
    }

    @Test
    void nullSortDirectionDefaultsToAscending() {
        TaskSort sort = new TaskSort(TaskSort.Field.TITLE, null);

        assertThat(sort.direction()).isEqualTo(TaskSort.Direction.ASC);
    }
}
