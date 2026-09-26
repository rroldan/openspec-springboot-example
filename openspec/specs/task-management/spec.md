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

### Requirement: Clients can list tasks with pagination and filters
The system SHALL expose `GET /api/v1/tasks` and return HTTP `200 OK` with a JSON page containing `items`, zero-based `page`, `size`, `totalElements`, and `totalPages`. The endpoint SHALL default to `page=0` and `size=20`, cap `size` at 100, and order results by `createdAt` descending, using `id` ascending to break timestamp ties. It SHALL support an optional `sort` parameter for one field, in the form `<field>,<direction>`, where `<field>` is `createdAt` or `title` and `<direction>` is `asc` or `desc`; when direction is omitted, it SHALL default to `asc`. When `sort` is omitted, the default order SHALL remain `createdAt` descending with `id` ascending as a tie-breaker. When sorting by `title`, `id` ascending SHALL break ties. The endpoint SHALL support an optional `status` filter matching one of `TODO`, `IN_PROGRESS`, or `DONE`, and an optional `q` filter matching a case-insensitive substring in the task title or description. When both filters are supplied, both SHALL apply. An empty `q` SHALL be treated as no text filter. Invalid status values, sort fields or directions, negative page values, or sizes below 1 or above 100 SHALL return HTTP `400 Bad Request` with the standard JSON error shape containing `errorId`, `message`, `timestamp`, and `correlationId`.

#### Scenario: List tasks without filters
- **GIVEN** tasks exist with different creation timestamps
- **WHEN** a client sends `GET /api/v1/tasks` without query parameters
- **THEN** the application returns HTTP `200 OK` with the first page of up to 20 tasks, ordered by `createdAt` descending, and reports page and total metadata in the response

#### Scenario: Filter tasks by status
- **GIVEN** tasks exist in multiple lifecycle statuses
- **WHEN** a client sends `GET /api/v1/tasks?status=DONE`
- **THEN** the application returns HTTP `200 OK` with only `DONE` tasks and page metadata describing the filtered result set

#### Scenario: Search task title and description
- **GIVEN** tasks exist whose titles or descriptions contain a search term with varying letter case
- **WHEN** a client sends `GET /api/v1/tasks?q=release`
- **THEN** the application returns HTTP `200 OK` with tasks containing `release` as a case-insensitive substring in either the title or description

#### Scenario: Combine status and text filters
- **GIVEN** tasks exist with varying statuses, titles, and descriptions
- **WHEN** a client sends `GET /api/v1/tasks?status=TODO&q=release`
- **THEN** the application returns HTTP `200 OK` with only tasks whose status is `TODO` and whose title or description contains `release` case-insensitively

#### Scenario: Request a later page
- **GIVEN** more matching tasks exist than fit on one page
- **WHEN** a client sends `GET /api/v1/tasks?page=1&size=10`
- **THEN** the application returns HTTP `200 OK` with at most 10 tasks for zero-based page 1 and metadata for the matching result set

#### Scenario: Sort tasks by title
- **GIVEN** tasks exist with different titles
- **WHEN** a client sends `GET /api/v1/tasks?sort=title,asc`
- **THEN** the application returns HTTP `200 OK` with tasks ordered by title ascending and `id` ascending for tasks with equal titles

#### Scenario: Sort tasks by creation time descending
- **GIVEN** tasks exist with different creation timestamps
- **WHEN** a client sends `GET /api/v1/tasks?sort=createdAt,desc`
- **THEN** the application returns HTTP `200 OK` with tasks ordered by creation time descending and `id` ascending for timestamp ties

#### Scenario: Reject invalid list parameters
- **GIVEN** the application is running
- **WHEN** a client sends `GET /api/v1/tasks` with an unsupported status, a negative page, or a size outside the range 1 through 100
- **THEN** the application returns HTTP `400 Bad Request` using the standard JSON error shape with `errorId`, `message`, `timestamp`, and `correlationId`

#### Scenario: Reject invalid sort values
- **GIVEN** the application is running
- **WHEN** a client sends `GET /api/v1/tasks` with a sort field other than `createdAt` or `title`, or a direction other than `asc` or `desc`
- **THEN** the application returns HTTP `400 Bad Request` using the standard JSON error shape with `errorId`, `message`, `timestamp`, and `correlationId`

#### Scenario: No tasks match the filters
- **GIVEN** no task matches the supplied filters
- **WHEN** a client sends `GET /api/v1/tasks` with those filters
- **THEN** the application returns HTTP `200 OK` with an empty `items` array and zero `totalElements` and `totalPages`

#### Scenario: OpenAPI documents task listing
- **GIVEN** a client retrieves the generated OpenAPI document
- **WHEN** the document is inspected
- **THEN** it describes `GET /api/v1/tasks`, its `status`, `q`, `page`, `size`, and `sort` query parameters, the paged task response, and its `400` standard error response
