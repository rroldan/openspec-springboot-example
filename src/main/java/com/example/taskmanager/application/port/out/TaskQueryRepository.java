package com.example.taskmanager.application.port.out;

import com.example.taskmanager.application.model.TaskPage;
import com.example.taskmanager.application.model.TaskSearchCriteria;

public interface TaskQueryRepository {

    TaskPage findAll(TaskSearchCriteria criteria);
}
