# Spec Delta

## ADDED Requirements

### Requirement: Clients can replace an existing task
The system SHALL expose `PUT /api/v1/tasks/{taskId}` to replace a task's title, description, and status from a JSON request. The request SHALL contain a non-blank `title`, a non-null `description`, and a valid status of `TODO`, `IN_PROGRESS`, or `DONE`. The system SHALL persist the replacement and return HTTP `200 OK` with the task representation. The task identifier and `createdAt` SHALL remain unchanged, and `updatedAt` SHALL be assigned by the server when the update is saved. An unknown task SHALL return HTTP `404 Not Found` with the standard JSON error shape; malformed identifiers and invalid or missing request fields SHALL return HTTP `400 Bad Request` with that shape.

#### Scenario: Valid replacement updates task fields
- **GIVEN** a task exists with an identifier and creation timestamp
- **WHEN** a client sends `PUT /api/v1/tasks/{taskId}` with a non-blank title, a description, and a valid status
- **THEN** the system returns HTTP `200 OK` with the saved task, the submitted title, description, and status, the original identifier and `createdAt`, and a server-assigned `updatedAt`

#### Scenario: Invalid or incomplete replacement is rejected
- **GIVEN** the application is running
- **WHEN** a client sends a PUT request with a blank title, a missing or null required field, or a status outside `TODO`, `IN_PROGRESS`, and `DONE`
- **THEN** the system returns HTTP `400 Bad Request` with the standard JSON error shape containing `errorId`, `message`, `timestamp`, and `correlationId`

#### Scenario: Unknown task cannot be updated
- **GIVEN** no task exists for the supplied valid identifier
- **WHEN** a client sends `PUT /api/v1/tasks/{taskId}` with a valid replacement request
- **THEN** the system returns HTTP `404 Not Found` with the standard JSON error shape and error identifier `TASK_NOT_FOUND`

#### Scenario: Malformed task identifier is rejected
- **GIVEN** the application is running
- **WHEN** a client sends `PUT /api/v1/tasks/not-a-uuid` with a valid replacement request
- **THEN** the system returns HTTP `400 Bad Request` with the standard JSON error shape containing `errorId`, `message`, `timestamp`, and `correlationId`

#### Scenario: OpenAPI documents task replacement
- **GIVEN** a client retrieves the generated OpenAPI document
- **WHEN** the document is inspected
- **THEN** it describes `PUT /api/v1/tasks/{taskId}`, the required replacement request fields, the `200` task response, and the standard `400` and `404` error responses
