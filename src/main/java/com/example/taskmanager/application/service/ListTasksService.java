package com.example.taskmanager.application.service;

import com.example.taskmanager.application.model.TaskPage;
import com.example.taskmanager.application.model.TaskSearchCriteria;
import com.example.taskmanager.application.model.TaskSort;
import com.example.taskmanager.application.port.in.ListTasksUseCase;
import com.example.taskmanager.application.port.out.TaskQueryRepository;

public class ListTasksService implements ListTasksUseCase {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final TaskSort DEFAULT_SORT =
            new TaskSort(TaskSort.Field.CREATED_AT, TaskSort.Direction.DESC);

    private final TaskQueryRepository taskQueryRepository;

    public ListTasksService(TaskQueryRepository taskQueryRepository) {
        this.taskQueryRepository = taskQueryRepository;
    }

    @Override
    public TaskPage list(TaskSearchCriteria criteria) {
        TaskSearchCriteria requested = criteria == null
                ? new TaskSearchCriteria(null, null, DEFAULT_PAGE, DEFAULT_SIZE, null)
                : criteria;
        TaskSearchCriteria normalized = new TaskSearchCriteria(
                requested.status(),
                requested.query() == null || requested.query().isEmpty() ? null : requested.query(),
                requested.page(),
                requested.size(),
                requested.sort() == null ? DEFAULT_SORT : requested.sort());

        return taskQueryRepository.findAll(normalized);
    }
}
