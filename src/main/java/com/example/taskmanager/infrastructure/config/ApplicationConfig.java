package com.example.taskmanager.infrastructure.config;

import com.example.taskmanager.application.port.in.GetApplicationInfoUseCase;
import com.example.taskmanager.application.port.in.CreateTaskUseCase;
import com.example.taskmanager.application.port.in.DeleteTaskUseCase;
import com.example.taskmanager.application.port.in.GetTaskByIdUseCase;
import com.example.taskmanager.application.port.in.ListTasksUseCase;
import com.example.taskmanager.application.port.in.UpdateTaskUseCase;
import com.example.taskmanager.application.port.in.UpdateTaskStatusUseCase;
import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.application.port.out.TaskQueryRepository;
import com.example.taskmanager.application.service.ApplicationInfoService;
import com.example.taskmanager.application.service.TaskCreationService;
import com.example.taskmanager.application.service.DeleteTaskService;
import com.example.taskmanager.application.service.GetTaskByIdService;
import com.example.taskmanager.application.service.ListTasksService;
import com.example.taskmanager.application.service.UpdateTaskService;
import com.example.taskmanager.application.service.UpdateTaskStatusService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ApplicationConfig {

    @Bean
    public GetApplicationInfoUseCase getApplicationInfoUseCase(
            @Value("${spring.application.name:task-manager}") String serviceName,
            @Value("${application.api.version:v1}") String apiVersion) {
        return new ApplicationInfoService(serviceName, apiVersion);
    }

    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepository taskRepository) {
        return new TaskCreationService(taskRepository, Clock.systemUTC());
    }

    @Bean
    public GetTaskByIdUseCase getTaskByIdUseCase(TaskRepository taskRepository) {
        return new GetTaskByIdService(taskRepository);
    }

    @Bean
    public DeleteTaskUseCase deleteTaskUseCase(TaskRepository taskRepository) {
        return new DeleteTaskService(taskRepository);
    }

    @Bean
    public ListTasksUseCase listTasksUseCase(TaskQueryRepository taskQueryRepository) {
        return new ListTasksService(taskQueryRepository);
    }

    @Bean
    public UpdateTaskUseCase updateTaskUseCase(TaskRepository taskRepository) {
        return new UpdateTaskService(taskRepository, Clock.systemUTC());
    }

    @Bean
    public UpdateTaskStatusUseCase updateTaskStatusUseCase(TaskRepository taskRepository) {
        return new UpdateTaskStatusService(taskRepository, Clock.systemUTC());
    }
}
