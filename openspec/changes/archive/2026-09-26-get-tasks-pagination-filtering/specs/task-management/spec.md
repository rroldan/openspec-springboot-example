# Spec Delta

## ADDED Requirements

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
