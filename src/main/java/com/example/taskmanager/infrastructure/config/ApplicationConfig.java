package com.example.taskmanager.infrastructure.config;

import com.example.taskmanager.application.port.in.GetApplicationInfoUseCase;
import com.example.taskmanager.application.port.in.CreateTaskUseCase;
import com.example.taskmanager.application.port.out.TaskRepository;
import com.example.taskmanager.application.service.ApplicationInfoService;
import com.example.taskmanager.application.service.TaskCreationService;
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
}
