# Spec Delta

## ADDED Requirements

### Requirement: Clients can update only a task's status
The system SHALL expose `PATCH /api/v1/tasks/{taskId}/status` to update a task's status from a JSON request containing a required `status` value of `TODO`, `IN_PROGRESS`, or `DONE`. The system SHALL persist the status change and return HTTP `200 OK` with the task representation. The task identifier, title, description, and `createdAt` SHALL remain unchanged; `updatedAt` SHALL be assigned by the server. The endpoint SHALL allow any transition between the supported statuses. An unknown task SHALL return HTTP `404 Not Found` with the standard JSON error shape and error identifier `TASK_NOT_FOUND`. A malformed identifier, missing or null status, or unsupported status SHALL return HTTP `400 Bad Request` with the standard JSON error shape and error identifier `INVALID_REQUEST`.

#### Scenario: Valid status update changes only status
- **GIVEN** a task exists with an identifier, title, description, status, and creation timestamp
- **WHEN** a client sends `PATCH /api/v1/tasks/{taskId}/status` with a JSON body containing a different supported status
- **THEN** the system returns HTTP `200 OK` with the task's new status, its original identifier, title, description, and `createdAt`, and a server-assigned `updatedAt`

#### Scenario: Same status can be submitted
- **GIVEN** a task exists with a supported status
- **WHEN** a client sends `PATCH /api/v1/tasks/{taskId}/status` with its current status
- **THEN** the system returns HTTP `200 OK` with the task representation and a server-assigned `updatedAt`

#### Scenario: Missing, null, or unsupported status is rejected
- **GIVEN** the application is running
- **WHEN** a client sends the status update request without a status, with a null status, or with a status outside `TODO`, `IN_PROGRESS`, and `DONE`
- **THEN** the system returns HTTP `400 Bad Request` with the standard JSON error body containing `errorId`, `message`, `timestamp`, and `correlationId`, and `errorId` is `INVALID_REQUEST`

#### Scenario: Unknown task cannot have its status updated
- **GIVEN** no task exists for the supplied valid identifier
- **WHEN** a client sends `PATCH /api/v1/tasks/{taskId}/status` with a supported status
- **THEN** the system returns HTTP `404 Not Found` with the standard JSON error body containing `errorId`, `message`, `timestamp`, and `correlationId`, and `errorId` is `TASK_NOT_FOUND`

#### Scenario: Malformed task identifier is rejected
- **GIVEN** the application is running
- **WHEN** a client sends `PATCH /api/v1/tasks/not-a-uuid/status` with a supported status
- **THEN** the system returns HTTP `400 Bad Request` with the standard JSON error body containing `errorId`, `message`, `timestamp`, and `correlationId`, and `errorId` is `INVALID_REQUEST`

#### Scenario: OpenAPI documents task status update
- **GIVEN** a client retrieves the generated OpenAPI document
- **WHEN** the document is inspected
- **THEN** it describes `PATCH /api/v1/tasks/{taskId}/status`, a required JSON status request limited to `TODO`, `IN_PROGRESS`, and `DONE`, the `200` task response, and the standard `400` and `404` error responses
