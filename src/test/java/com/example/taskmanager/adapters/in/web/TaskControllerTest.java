package com.example.taskmanager.adapters.in.web;

import com.example.taskmanager.application.model.TaskPage;
import com.example.taskmanager.application.port.in.CreateTaskUseCase;
import com.example.taskmanager.application.port.in.DeleteTaskUseCase;
import com.example.taskmanager.application.port.in.GetTaskByIdUseCase;
import com.example.taskmanager.application.port.in.ListTasksUseCase;
import com.example.taskmanager.application.port.in.UpdateTaskUseCase;
import com.example.taskmanager.application.port.in.UpdateTaskStatusUseCase;
import com.example.taskmanager.application.service.TaskNotFoundException;
import com.example.taskmanager.domain.model.Task;
import com.example.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private CreateTaskUseCase useCase;

    @Mock
    private DeleteTaskUseCase deleteTaskUseCase;

    @Mock
    private GetTaskByIdUseCase getTaskByIdUseCase;

    @Mock
    private ListTasksUseCase listTasksUseCase;

    @Mock
    private UpdateTaskUseCase updateTaskUseCase;

    @Mock
    private UpdateTaskStatusUseCase updateTaskStatusUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new TaskController(useCase, deleteTaskUseCase, getTaskByIdUseCase,
                                listTasksUseCase, updateTaskUseCase, updateTaskStatusUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listsTasks() throws Exception {
        Instant timestamp = Instant.parse("2026-01-01T12:00:00Z");
        Task task = new Task(UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f"),
                "title", "description", TaskStatus.TODO, timestamp, timestamp);
        TaskPage page = new TaskPage(java.util.List.of(task), 0, 20, 1, 1);
        when(listTasksUseCase.list(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].title").value("title"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void rejectsNegativePage() throws Exception {
        mockMvc.perform(get("/api/v1/tasks").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"));
    }

    @Test
    void rejectsSizeBelowMinimum() throws Exception {
        mockMvc.perform(get("/api/v1/tasks").param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"));
    }

    @Test
    void rejectsSizeAboveMaximum() throws Exception {
        mockMvc.perform(get("/api/v1/tasks").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"));
    }

    @Test
    void rejectsInvalidStatus() throws Exception {
        mockMvc.perform(get("/api/v1/tasks").param("status", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"));
    }

    @Test
    void rejectsInvalidSortField() throws Exception {
        mockMvc.perform(get("/api/v1/tasks").param("sort", "invalid,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"));
    }

    @Test
    void rejectsInvalidSortDirection() throws Exception {
        mockMvc.perform(get("/api/v1/tasks").param("sort", "created_at,invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"));
    }

    @Test
    void createsTask() throws Exception {
        Instant timestamp = Instant.parse("2026-01-01T12:00:00Z");
        when(useCase.create(anyString(), anyString())).thenReturn(new Task(
                UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f"),
                "title", "description", TaskStatus.TODO, timestamp, timestamp));

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType("application/json")
                        .content("""
                                {"title":"title","description":"description","status":"DONE"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.createdAt").value("2026-01-01T12:00:00Z"));
    }

    @Test
    void rejectsBlankTitle() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType("application/json")
                        .content("""
                                {"title":" ","description":"description"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString());
    }

    @Test
    void getsTaskById() throws Exception {
        UUID id = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
        Instant timestamp = Instant.parse("2026-01-01T12:00:00Z");
        when(getTaskByIdUseCase.getById(id)).thenReturn(new Task(
                id, "title", "description", TaskStatus.TODO, timestamp, timestamp));

        mockMvc.perform(get("/api/v1/tasks/{taskId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void replacesTask() throws Exception {
        UUID id = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
        Instant createdAt = Instant.parse("2026-01-01T12:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-02T12:00:00Z");
        when(updateTaskUseCase.update(id, "new title", "new description", TaskStatus.IN_PROGRESS))
                .thenReturn(new Task(id, "new title", "new description", TaskStatus.IN_PROGRESS,
                        createdAt, updatedAt));

        mockMvc.perform(put("/api/v1/tasks/{taskId}", id)
                        .contentType("application/json")
                        .content("""
                                {"title":"new title","description":"new description","status":"IN_PROGRESS"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("new title"))
                .andExpect(jsonPath("$.description").value("new description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.createdAt").value("2026-01-01T12:00:00Z"))
                .andExpect(jsonPath("$.updatedAt").value("2026-01-02T12:00:00Z"));
    }

    @Test
    void rejectsBlankTitleWhenReplacingTask() throws Exception {
        mockMvc.perform(put("/api/v1/tasks/{taskId}", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {"title":" ","description":"description","status":"TODO"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString());
    }

    @Test
    void rejectsMissingDescriptionWhenReplacingTask() throws Exception {
        mockMvc.perform(put("/api/v1/tasks/{taskId}", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {"title":"title","status":"TODO"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"));
    }

    @Test
    void rejectsNullStatusWhenReplacingTask() throws Exception {
        mockMvc.perform(put("/api/v1/tasks/{taskId}", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {"title":"title","description":"description","status":null}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"));
    }

    @Test
    void rejectsUnknownStatusWhenReplacingTask() throws Exception {
        mockMvc.perform(put("/api/v1/tasks/{taskId}", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {"title":"title","description":"description","status":"INVALID"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"));
    }

    @Test
    void returnsNotFoundWhenReplacingUnknownTask() throws Exception {
        UUID id = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
        when(updateTaskUseCase.update(id, "title", "description", TaskStatus.TODO))
                .thenThrow(new TaskNotFoundException(id));

        mockMvc.perform(put("/api/v1/tasks/{taskId}", id)
                        .contentType("application/json")
                        .content("""
                                {"title":"title","description":"description","status":"TODO"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorId").value("TASK_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString());
    }

    @Test
    void rejectsMalformedIdWhenReplacingTask() throws Exception {
        mockMvc.perform(put("/api/v1/tasks/not-a-uuid")
                        .contentType("application/json")
                        .content("""
                                {"title":"title","description":"description","status":"TODO"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString());
    }

    @Test
    void updatesOnlyTaskStatus() throws Exception {
        UUID id = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
        Instant createdAt = Instant.parse("2026-01-01T12:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-02T12:00:00Z");
        when(updateTaskStatusUseCase.updateStatus(id, TaskStatus.IN_PROGRESS))
                .thenReturn(new Task(id, "original title", "original description",
                        TaskStatus.IN_PROGRESS, createdAt, updatedAt));

        mockMvc.perform(patch("/api/v1/tasks/{taskId}/status", id)
                        .contentType("application/json")
                        .content("""
                                {"status":"IN_PROGRESS"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("original title"))
                .andExpect(jsonPath("$.description").value("original description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.createdAt").value("2026-01-01T12:00:00Z"))
                .andExpect(jsonPath("$.updatedAt").value("2026-01-02T12:00:00Z"));
    }

    @Test
    void rejectsMissingNullAndUnsupportedStatus() throws Exception {
        for (String body : new String[]{"{}", "{\"status\":null}", "{\"status\":\"INVALID\"}"}) {
            mockMvc.perform(patch("/api/v1/tasks/{taskId}/status", UUID.randomUUID())
                            .contentType("application/json")
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.correlationId").isString());
        }
    }

    @Test
    void returnsNotFoundWhenUpdatingStatusForUnknownTask() throws Exception {
        UUID id = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
        when(updateTaskStatusUseCase.updateStatus(id, TaskStatus.DONE))
                .thenThrow(new TaskNotFoundException(id));

        mockMvc.perform(patch("/api/v1/tasks/{taskId}/status", id)
                        .contentType("application/json")
                        .content("""
                                {"status":"DONE"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorId").value("TASK_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString());
    }

    @Test
    void rejectsMalformedIdWhenUpdatingTaskStatus() throws Exception {
        mockMvc.perform(patch("/api/v1/tasks/not-a-uuid/status")
                        .contentType("application/json")
                        .content("""
                                {"status":"DONE"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString());
    }

    @Test
    void returnsNotFoundForUnknownTask() throws Exception {
        UUID id = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
        when(getTaskByIdUseCase.getById(id)).thenThrow(new TaskNotFoundException(id));

        mockMvc.perform(get("/api/v1/tasks/{taskId}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorId").value("TASK_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString());
    }

    @Test
    void rejectsMalformedTaskId() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString());
    }

    @Test
    void deletesTask() throws Exception {
        UUID id = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");

        mockMvc.perform(delete("/api/v1/tasks/{taskId}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(deleteTaskUseCase).delete(id);
    }

    @Test
    void returnsNotFoundWhenDeletingUnknownTask() throws Exception {
        UUID id = UUID.fromString("3f6f0f2e-3f2c-4b61-8c1b-0b3d4c5d6e7f");
        doThrow(new TaskNotFoundException(id)).when(deleteTaskUseCase).delete(id);

        mockMvc.perform(delete("/api/v1/tasks/{taskId}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorId").value("TASK_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString());
    }

    @Test
    void rejectsMalformedTaskIdWhenDeleting() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorId").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.correlationId").isString());
    }
}
