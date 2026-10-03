# Spec Delta

## ADDED Requirements

### Requirement: Clients can delete tasks
The system SHALL expose `DELETE /api/v1/tasks/{taskId}` to delete the task identified by a valid UUID. When the task exists, the system SHALL delete it and return HTTP `204 No Content` with no response body. When no task exists for the valid UUID, the system SHALL return HTTP `404 Not Found` with the standard JSON error shape and error identifier `TASK_NOT_FOUND`. A malformed identifier SHALL return HTTP `400 Bad Request` with the standard JSON error shape and error identifier `INVALID_REQUEST`.

#### Scenario: Delete an existing task
- **GIVEN** a task exists with the supplied valid UUID
- **WHEN** a client sends `DELETE /api/v1/tasks/{taskId}`
- **THEN** the system deletes the task and returns HTTP `204 No Content` with an empty response body

#### Scenario: Delete an unknown task
- **GIVEN** no task exists for the supplied valid UUID
- **WHEN** a client sends `DELETE /api/v1/tasks/{taskId}`
- **THEN** the system returns HTTP `404 Not Found` with the standard JSON error body containing `errorId`, `message`, `timestamp`, and `correlationId`, and `errorId` is `TASK_NOT_FOUND`

#### Scenario: Reject a malformed task identifier
- **GIVEN** the application is running
- **WHEN** a client sends `DELETE /api/v1/tasks/not-a-uuid`
- **THEN** the system returns HTTP `400 Bad Request` with the standard JSON error body containing `errorId`, `message`, `timestamp`, and `correlationId`, and `errorId` is `INVALID_REQUEST`

#### Scenario: OpenAPI documents task deletion
- **GIVEN** a client retrieves the generated OpenAPI document
- **WHEN** the document is inspected
- **THEN** it describes `DELETE /api/v1/tasks/{taskId}` with a `204` response and the standard `400` and `404` error responses
