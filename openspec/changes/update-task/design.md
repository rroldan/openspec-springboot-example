# Design

## Context

See `proposal.md` for motivation and `specs/task-management/spec.md` for the observable contract. The existing task API is rooted at `/api/v1/tasks`; the controller delegates to application use cases, which use `TaskRepository`. The repository already supports lookup and save, and the domain task contains all updateable fields plus server-managed timestamps.

## Goals / Non-Goals

**Goals:**
- Implement replacement through the existing controller-to-use-case-to-repository boundaries.
- Preserve the persisted task identity and creation time while assigning the update time on the server.
- Reuse existing validation, not-found, error, and API documentation conventions.

**Non-Goals:**
- Add partial-update semantics, lifecycle transition rules, or optimistic locking.
- Change the task table, add a Flyway migration, or introduce dependencies or Spring beans beyond the update use case.

## Decisions

- **Add a dedicated update request and input use case.** The request contains required `title`, `description`, and `TaskStatus` fields, keeping server-owned fields out of the writable contract. A dedicated use case keeps update behavior separate from creation and retrieval while following the existing application-port pattern.
- **Load, replace, and save through `TaskRepository`.** The service finds the current task, throws the existing `TaskNotFoundException` when absent, then saves a new domain task using the existing ID and `createdAt`, request values, and the injected `Clock` for `updatedAt`. This reuses existing persistence behavior without adding a repository operation or changing the entity.
- **Expose HTTP 200 and document the operation on the existing controller.** The controller maps the request to the use case and returns the saved `TaskResponse`. Existing exception handling supplies the standard 400 and 404 payloads. OpenAPI annotations and the generated snapshot should include the required request fields and 200/400/404 responses.
- **Treat each valid status as directly assignable.** The current domain defines the allowed status set but no transition graph; the update endpoint accepts any of its three values rather than introducing new lifecycle rules.

## Risks / Trade-offs

- [Concurrent updates are last-write-wins because the current entity has no version field] → Keep concurrency control out of scope and preserve the existing persistence model; add optimistic locking only if a separate requirement establishes that behavior.
- [A full replacement requires clients to submit every writable field] → Document required request fields in OpenAPI and return the existing 400 response for missing or invalid values.

## Migration Plan

No database migration is required. Deploy the additive endpoint with the application; rollback consists of reverting the application release because no persisted schema changes are introduced.
