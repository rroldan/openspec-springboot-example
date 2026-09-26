package com.example.taskmanager.adapters.in.web;

import com.example.taskmanager.application.model.TaskPage;
import com.example.taskmanager.application.port.in.CreateTaskUseCase;
import com.example.taskmanager.application.port.in.GetTaskByIdUseCase;
import com.example.taskmanager.application.port.in.ListTasksUseCase;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private CreateTaskUseCase useCase;

    @Mock
    private GetTaskByIdUseCase getTaskByIdUseCase;

    @Mock
    private ListTasksUseCase listTasksUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(useCase, getTaskByIdUseCase, listTasksUseCase))
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
}
