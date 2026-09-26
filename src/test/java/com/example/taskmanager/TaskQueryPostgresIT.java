package com.example.taskmanager;

import com.example.taskmanager.adapters.out.persistence.SpringDataTaskRepository;
import com.example.taskmanager.adapters.out.persistence.TaskJpaEntity;
import com.example.taskmanager.application.model.TaskPage;
import com.example.taskmanager.application.model.TaskSearchCriteria;
import com.example.taskmanager.application.model.TaskSort;
import com.example.taskmanager.application.port.out.TaskQueryRepository;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class TaskQueryPostgresIT {

    private static final List<UUID> FIXTURE_IDS = List.of(
            UUID.fromString("a0000000-0000-0000-0000-000000000001"),
            UUID.fromString("a0000000-0000-0000-0000-000000000002"),
            UUID.fromString("a0000000-0000-0000-0000-000000000003"),
            UUID.fromString("a0000000-0000-0000-0000-000000000004"),
            UUID.fromString("a0000000-0000-0000-0000-000000000005"));
    private static final Instant BASE_TIME = Instant.parse("2026-01-01T12:00:00Z");

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("taskmanager")
                    .withUsername("taskmanager")
                    .withPassword("taskmanager");

    @Autowired
    private TaskQueryRepository taskQueryRepository;

    @Autowired
    private SpringDataTaskRepository taskRepository;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void cleanBeforeTest() {
        removeFixtures();
    }

    @AfterEach
    void cleanAfterTest() {
        removeFixtures();
    }

    private void removeFixtures() {
        taskRepository.deleteAllById(FIXTURE_IDS);
    }

    @Test
    void filtersByStatusAndCaseInsensitiveTitleOrDescriptionSearch() {
        save(0, "MiXeD in title", "unrelated description", TaskStatus.TODO, 0);
        save(1, "unrelated title", "description contains mIxEd", TaskStatus.TODO, 1);
        save(2, "MiXeD but completed", "unrelated description", TaskStatus.DONE, 2);
        save(3, "unrelated title", "unrelated description", TaskStatus.TODO, 3);

        TaskPage result = taskQueryRepository.findAll(new TaskSearchCriteria(
                TaskStatus.TODO,
                "MIXED",
                0,
                10,
                new TaskSort(TaskSort.Field.TITLE, TaskSort.Direction.ASC)));

        assertThat(ids(result)).containsExactly(FIXTURE_IDS.get(0), FIXTURE_IDS.get(1));
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void ordersDeterministicallyByRequestedAndDefaultSortWhenKeysTie() {
        save(0, "Same title", "order-marker", TaskStatus.TODO, 2);
        save(1, "Same title", "order-marker", TaskStatus.TODO, 2);
        save(2, "Other title", "order-marker", TaskStatus.TODO, 3);
        save(3, "Other title", "order-marker", TaskStatus.TODO, 1);

        TaskPage titleSorted = taskQueryRepository.findAll(new TaskSearchCriteria(
                null,
                "order-marker",
                0,
                10,
                new TaskSort(TaskSort.Field.TITLE, TaskSort.Direction.ASC)));
        TaskPage defaultSorted = taskQueryRepository.findAll(new TaskSearchCriteria(
                null, "order-marker", 0, 10, null));

        assertThat(ids(titleSorted)).containsExactly(
                FIXTURE_IDS.get(2), FIXTURE_IDS.get(3),
                FIXTURE_IDS.get(0), FIXTURE_IDS.get(1));
        assertThat(ids(defaultSorted)).containsExactly(
                FIXTURE_IDS.get(2), FIXTURE_IDS.get(0),
                FIXTURE_IDS.get(1), FIXTURE_IDS.get(3));
    }

    @Test
    void returnsConsistentMetadataAcrossMultiplePages() {
        save(0, "Page A", "pagecase", TaskStatus.IN_PROGRESS, 0);
        save(1, "Page B", "pagecase", TaskStatus.IN_PROGRESS, 1);
        save(2, "Page C", "pagecase", TaskStatus.IN_PROGRESS, 2);
        save(3, "Page D", "pagecase", TaskStatus.IN_PROGRESS, 3);
        save(4, "Page E", "pagecase", TaskStatus.IN_PROGRESS, 4);

        TaskPage firstPage = queryPage(0, 2);
        TaskPage secondPage = queryPage(1, 2);
        TaskPage finalPage = queryPage(2, 2);

        assertThat(ids(firstPage)).containsExactly(FIXTURE_IDS.get(0), FIXTURE_IDS.get(1));
        assertThat(ids(secondPage)).containsExactly(FIXTURE_IDS.get(2), FIXTURE_IDS.get(3));
        assertThat(ids(finalPage)).containsExactly(FIXTURE_IDS.get(4));
        for (TaskPage page : List.of(firstPage, secondPage, finalPage)) {
            assertThat(page.size()).isEqualTo(2);
            assertThat(page.totalElements()).isEqualTo(5);
            assertThat(page.totalPages()).isEqualTo(3);
        }
        assertThat(firstPage.page()).isZero();
        assertThat(secondPage.page()).isEqualTo(1);
        assertThat(finalPage.page()).isEqualTo(2);
    }

    @Test
    void returnsEmptyPageMetadataWhenNothingMatches() {
        TaskPage result = taskQueryRepository.findAll(new TaskSearchCriteria(
                TaskStatus.DONE,
                "no-task-has-this-unique-query",
                1,
                3,
                null));

        assertThat(result.items()).isEmpty();
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
    }

    private TaskPage queryPage(int page, int size) {
        return taskQueryRepository.findAll(new TaskSearchCriteria(
                TaskStatus.IN_PROGRESS,
                "PAGECASE",
                page,
                size,
                new TaskSort(TaskSort.Field.TITLE, TaskSort.Direction.ASC)));
    }

    private void save(int fixtureIndex, String title, String description, TaskStatus status,
                      int secondsAfterBase) {
        Instant createdAt = BASE_TIME.plusSeconds(secondsAfterBase);
        taskRepository.save(new TaskJpaEntity(
                FIXTURE_IDS.get(fixtureIndex),
                title,
                description,
                status,
                createdAt,
                createdAt));
    }

    private static List<UUID> ids(TaskPage page) {
        return page.items().stream().map(task -> task.id()).toList();
    }
}
