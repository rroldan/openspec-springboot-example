package com.example.taskmanager.application.port.in;

import com.example.taskmanager.application.model.TaskPage;
import com.example.taskmanager.application.model.TaskSearchCriteria;

public interface ListTasksUseCase {

    TaskPage list(TaskSearchCriteria criteria);
}
