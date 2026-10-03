# Design

## Context

The task API is rooted at `/api/v1/tasks`; `TaskController` currently exposes create, get, list, and replace operations. Application use cases are defined by input ports and explicitly registered as beans in `ApplicationConfig`. The `TaskRepository` output port currently supports save and lookup, while `TaskPersistenceAdapter` delegates to a Spring Data JPA repository. `GlobalExceptionHandler` maps `TaskNotFoundException` to the established `TASK_NOT_FOUND` response and malformed UUIDs to `INVALID_REQUEST`.

See `proposal.md` for motivation and `specs/task-management/spec.md` for the externally visible contract.

## Goals / Non-Goals

**Goals:**
- Keep deletion within the existing controller → input port/service → output port → persistence adapter boundaries.
- Reuse established UUID conversion and task-not-found error handling.
- Verify deletion at the web, application, persistence, integration, and OpenAPI contract levels.

**Non-Goals:**
- Soft deletion, task restoration, or changes to task representation and lifecycle fields.
- Changes to authorization policy or unrelated task operations.

## Decisions

- Add a dedicated delete input port and application service, registered as a bean in `ApplicationConfig`. This follows the explicit use-case wiring already used by task creation, lookup, and update.
- Extend `TaskRepository` with a delete operation. The service first looks up the UUID and throws the existing `TaskNotFoundException` when absent, then requests deletion through the port; the persistence adapter delegates removal to Spring Data JPA. This preserves the existing not-found contract rather than relying on `deleteById`, which does not report an absent row.
- Add `DELETE /{taskId}` to `TaskController`, accepting `UUID` as the path variable so malformed identifiers continue through the global `400 INVALID_REQUEST` mapping. Return an empty `204` response on success and annotate the operation's `204`, `400`, and `404` responses for generated OpenAPI.
- Use a hard delete from the existing `tasks` table. No domain/JPA field changes, Flyway migration, dependency, or additional Spring bean infrastructure are needed.
- Test the service's present/missing-task behavior, the persistence delegation, controller response/error behavior, persisted deletion in a PostgreSQL integration test, and the generated OpenAPI operation/snapshot.

## Risks / Trade-offs

- [Deletion is permanent and has no restore path] → Keep the API contract explicit; archival and recovery are outside this change.
- [A concurrent delete can occur between the service's existence lookup and persistence delete] → Treat DELETE as idempotent at the storage operation; the request that observes the task absent before deletion receives the specified 404.

## Migration Plan

No database migration is required because deletion uses the existing task table. Deploy the application change normally. Rolling back the code removes the endpoint but cannot restore tasks already deleted.
