# Spec Delta

## Purpose

Provides the first task-management capability for creating durable tasks through the versioned REST API, with controlled lifecycle status and server-managed audit timestamps.

## ADDED Requirements

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
