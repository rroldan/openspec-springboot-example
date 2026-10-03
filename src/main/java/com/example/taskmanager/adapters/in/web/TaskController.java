package com.example.taskmanager.adapters.in.web;

import com.example.taskmanager.application.port.in.CreateTaskUseCase;
import com.example.taskmanager.application.port.in.DeleteTaskUseCase;
import com.example.taskmanager.application.port.in.GetTaskByIdUseCase;
import com.example.taskmanager.application.port.in.ListTasksUseCase;
import com.example.taskmanager.application.port.in.UpdateTaskUseCase;
import com.example.taskmanager.application.port.in.UpdateTaskStatusUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final GetTaskByIdUseCase getTaskByIdUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;

    public TaskController(CreateTaskUseCase createTaskUseCase, DeleteTaskUseCase deleteTaskUseCase,
                          GetTaskByIdUseCase getTaskByIdUseCase,
                          ListTasksUseCase listTasksUseCase, UpdateTaskUseCase updateTaskUseCase,
                          UpdateTaskStatusUseCase updateTaskStatusUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.getTaskByIdUseCase = getTaskByIdUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.updateTaskStatusUseCase = updateTaskStatusUseCase;
    }

    @GetMapping
    @Operation(summary = "List tasks", description = "Returns a paginated list of tasks with optional filtering and sorting.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks found",
                    content = @Content(schema = @Schema(implementation = TaskListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<TaskListResponse> listTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        // Validate and map parameters
        if (page < 0) {
            throw new IllegalArgumentException("page must be non-negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }

        // Parse status filter
        com.example.taskmanager.domain.model.TaskStatus parsedStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                parsedStatus = com.example.taskmanager.domain.model.TaskStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("invalid status: " + status);
            }
        }

        // Parse sort parameter
        com.example.taskmanager.application.model.TaskSort parsedSort = null;
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            if (parts.length > 2 || parts.length == 0) {
                throw new IllegalArgumentException("invalid sort format");
            }
            
            String field = parts[0].trim();
            String direction = parts.length > 1 ? parts[1].trim() : "asc";
            
            com.example.taskmanager.application.model.TaskSort.Field sortField;
            try {
                sortField = com.example.taskmanager.application.model.TaskSort.Field.valueOf(field.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("invalid sort field: " + field);
            }
            
            com.example.taskmanager.application.model.TaskSort.Direction sortDirection;
            try {
                sortDirection = com.example.taskmanager.application.model.TaskSort.Direction.valueOf(direction.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("invalid sort direction: " + direction);
            }
            
            parsedSort = new com.example.taskmanager.application.model.TaskSort(sortField, sortDirection);
        }

        // Normalize empty q to null
        String normalizedQ = (q != null && !q.isBlank()) ? q : null;

        com.example.taskmanager.application.model.TaskSearchCriteria criteria =
                new com.example.taskmanager.application.model.TaskSearchCriteria(
                        parsedStatus, normalizedQ, page, size, parsedSort);

        return ResponseEntity.ok(TaskListResponse.from(listTasksUseCase.list(criteria)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a task", description = "Creates a task with server-managed status and timestamps.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(201)
                .body(TaskResponse.from(createTaskUseCase.create(request.title(), request.description())));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get a task by ID", description = "Returns a persisted task by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid task identifier",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID taskId) {
        return ResponseEntity.ok(TaskResponse.from(getTaskByIdUseCase.getById(taskId)));
    }

    @PutMapping(path = "/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Replace a task", description = "Replaces a task's title, description, and status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId, @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(TaskResponse.from(updateTaskUseCase.update(
                taskId, request.title(), request.description(), request.status())));
    }

    @PatchMapping(path = "/{taskId}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a task's status", description = "Changes only a task's lifecycle status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task status updated",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable UUID taskId, @Valid @RequestBody UpdateTaskStatusRequest request) {
        return ResponseEntity.ok(TaskResponse.from(
                updateTaskStatusUseCase.updateStatus(taskId, request.status())));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a task", description = "Permanently deletes a task by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid task identifier",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        deleteTaskUseCase.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}
