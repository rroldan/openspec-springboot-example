# Spec Delta

## ADDED Requirements

### Requirement: Clients can retrieve a task by its identifier
The system SHALL expose `GET /api/v1/tasks/{taskId}` and SHALL return the persisted task representation for a valid task identifier. When the task does not exist, the system SHALL return HTTP `404 Not Found` using the standard JSON error shape. When the supplied identifier is malformed, the system SHALL return HTTP `400 Bad Request` with the standard validation error payload.

#### Scenario: Lookup succeeds for an existing task
- **GIVEN** a task exists in the database with a valid identifier
- **WHEN** a client sends `GET /api/v1/tasks/{taskId}`
- **THEN** the application returns HTTP `200 OK` with the task payload including the identifier, title, description, status, `createdAt`, and `updatedAt` values

#### Scenario: Unknown task identifier is rejected
- **GIVEN** no task exists for the supplied identifier
- **WHEN** a client sends `GET /api/v1/tasks/{taskId}`
- **THEN** the application returns HTTP `404 Not Found` using the standard error structure with a machine-readable error identifier and correlation or timestamp metadata

#### Scenario: Malformed identifier is rejected
- **GIVEN** the application is running
- **WHEN** a client sends `GET /api/v1/tasks/not-a-uuid`
- **THEN** the application returns HTTP `400 Bad Request` with the standard validation error payload
