# task-management Specification

## Purpose
Provides the first task-management capability for creating durable tasks through the versioned REST API, with controlled lifecycle status and server-managed audit timestamps.

## Requirements

### Requirement: Clients can create tasks
The system SHALL expose `POST /api/v1/tasks` to create a task from a JSON request containing a non-blank `title` and a `description`, and SHALL return the persisted task representation.

#### Scenario: Valid task creation succeeds
- **GIVEN** the application is running and the persistence dependencies are available
- **WHEN** a client sends `POST /api/v1/tasks` with a valid title and description
- **THEN** the application returns HTTP `201 Created` with a JSON task containing a stable identifier, the submitted title and description, status `TODO`, and populated `createdAt` and `updatedAt` timestamps

#### Scenario: Missing or blank title is rejected
- **GIVEN** the application is running
- **WHEN** a client sends `POST /api/v1/tasks` with a missing or blank `title`
- **THEN** the application returns HTTP `400 Bad Request` using the standard JSON error shape with a machine-readable error identifier, a human-readable validation message, and correlation or timestamp metadata

#### Scenario: Client cannot override server-managed creation fields
- **GIVEN** the application is running
- **WHEN** a client sends a create request containing `status`, `createdAt`, or `updatedAt` values
- **THEN** the application ignores those client-supplied lifecycle values and creates the task with status `TODO` and timestamps assigned by the server

### Requirement: Tasks have a controlled lifecycle representation
The system SHALL represent every task with a unique identifier, title, description, one of the statuses `TODO`, `IN_PROGRESS`, or `DONE`, and server-managed creation and last-update timestamps.

#### Scenario: Newly persisted tasks use the initial status
- **GIVEN** a valid task creation request
- **WHEN** the task is persisted
- **THEN** its status is `TODO`, its `createdAt` is set, and its `updatedAt` is set to the same creation instant or a later instant

#### Scenario: Task response exposes the documented representation
- **GIVEN** a task was created successfully
- **WHEN** the client reads the create response
- **THEN** the JSON representation includes the task identifier, title, description, status, `createdAt`, and `updatedAt` fields with documented schemas

### Requirement: Task creation is documented and durable
The system SHALL persist created tasks in PostgreSQL through the standard migration process and SHALL describe the create-task request, success response, validation error response, and task schemas in the generated OpenAPI document.

#### Scenario: Task table is available after a fresh migration
- **GIVEN** an empty PostgreSQL schema with valid application configuration
- **WHEN** the application starts and Flyway runs
- **THEN** the task persistence schema is created successfully before the task endpoint serves requests

#### Scenario: OpenAPI documents task creation
- **GIVEN** the application is running
- **WHEN** a client retrieves the generated OpenAPI document
- **THEN** the document contains `POST /api/v1/tasks`, its `201` response, its `400` error response, and the request and task response schemas

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
