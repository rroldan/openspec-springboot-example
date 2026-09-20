# Design

## Context

See `proposal.md` for the motivation. The current service has a versioned application-info controller, Clean Architecture package boundaries, PostgreSQL/Flyway configuration, centralized JSON error handling, and no task domain model or persistence schema.

## Goals / Non-Goals

**Goals:**

- Add a task creation use case exposed through the existing `/api/v1` REST convention.
- Keep domain and application logic independent of Spring MVC and JPA.
- Persist task identity, content, lifecycle status, and timestamps in PostgreSQL.
- Make validation failures and the OpenAPI contract consistent with the existing API.

**Non-Goals:**

- Listing, editing, deleting, or filtering tasks.
- User ownership, authentication, authorization, or multi-tenant behavior.
- Client-controlled status transitions or client-controlled timestamps.

## Decisions

### Layered task creation flow

Add a task domain model and status enum, an input port/use case, an application service, a persistence output port, and web/persistence adapters. The controller maps request and response DTOs; the service assigns `TODO` and timestamps; the repository adapter maps the domain object to a JPA entity. This follows the existing Clean Architecture direction rather than allowing controllers or entities to contain the use-case behavior.

Alternative considered: place the creation logic directly in a Spring Data repository-backed controller. This is rejected because it would violate the project's architecture boundary and make unit testing the behavior less isolated.

### REST contract

Use `POST /api/v1/tasks` with a request DTO containing `title` and `description`. Return `201 Created` with a response DTO containing `id`, `title`, `description`, `status`, `createdAt`, and `updatedAt`. Treat `title` as required and non-blank; keep description accepted as the submitted task text. Reuse the existing global exception handler and error DTO for validation failures.

Alternative considered: accept status and timestamps in the request. This is rejected because those fields are lifecycle metadata owned by the server and would allow clients to create inconsistent task state.

### Persistence and migration

Add a Flyway migration creating a `tasks` table with a generated primary key, non-null title, description storage, constrained status text, and timestamp columns. Add a JPA entity and repository adapter behind the persistence output port. No new runtime dependency or Spring bean type is required beyond the existing Spring Data JPA infrastructure.

Alternative considered: store tasks in memory while the API is introduced. This is rejected because the project already establishes PostgreSQL and Flyway as its persistence baseline, and in-memory state would disappear across restarts.

### OpenAPI and testing

Annotate the controller and DTOs so springdoc exposes request, response, and validation error schemas. Add focused service/controller unit tests, architecture assertions for the new package dependencies, a Testcontainers PostgreSQL integration test covering migration and creation, and mutation coverage for validation/status/timestamp behavior.

## Risks / Trade-offs

- [Risk] The initial schema fixes status values before future workflow requirements are known -> Mitigation: constrain only the three requested values and keep status transitions out of this change.
- [Risk] Timestamp precision or database-generated values can differ from the response object -> Mitigation: assign timestamps in the application service and verify persistence/response consistency in integration tests.
- [Risk] Existing error handling may not yet cover Bean Validation details -> Mitigation: adapt the centralized handler with an explicit validation mapping and test the documented error shape.

## Migration Plan

Apply the new Flyway migration during normal application startup. Deploy the application and migration together; rollback requires reverting the application and, if the database is being restored, dropping the task table through the project's controlled database rollback process. No existing tables or endpoint contracts are changed.
