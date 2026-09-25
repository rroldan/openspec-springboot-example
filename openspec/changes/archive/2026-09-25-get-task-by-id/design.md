# Design

## Context

The application already has the task aggregate, repository abstraction, and create-task controller path. This change extends the same capability by adding a read-only lookup for a single task without changing the persistence model or database schema.

## Goals / Non-Goals

**Goals:**
- Expose a read endpoint for a single task by UUID
- Reuse the existing domain model and repository abstraction
- Return consistent JSON error responses for not-found and malformed-ID cases

**Non-Goals:**
- Listing all tasks or paginated task queries
- Updating or deleting tasks
- Any migration or schema redesign beyond the current task table

## Decisions

- Use a dedicated read use case (`GetTaskByIdUseCase`) and service that delegates to `TaskRepository.findById(UUID)`. This matches the existing clean-architecture pattern used for task creation and keeps the controller thin.
- Keep the `Task` aggregate unchanged; the lookup only reads an existing record and serializes it to the same response representation already used for task creation. This avoids introducing a new domain entity or altering persisted data.
- Handle missing tasks at the service boundary by throwing a domain-specific not-found exception, and translate that at the controller/advice layer into the standard `404` JSON error payload. This preserves error formatting consistency while isolating business logic from HTTP concerns.
- Validate path IDs as UUIDs at the HTTP boundary to reject malformed identifiers before repository access. The invalid path input is treated as a client error and is consistent with the existing validation response contract.

## Risks / Trade-offs

- [Missing task lookup forces every caller to handle `404` explicitly] → Mitigation: use a standard API error mapping and document the response contract in OpenAPI.
- [Repository lookup may cause uncontrolled null handling if not centralized] → Mitigation: encapsulate lookup in the service with a single not-found exception path.
- [Malformed UUID input can bypass service logic if controller validation is weak] → Mitigation: enforce UUID validation in the endpoint contract and cover it with tests.

## Migration Plan

No database migration is required because the task table already exists and the feature reads from it. The change is additive and can be rolled back safely by removing the endpoint and its corresponding use case without affecting create-task behavior.

## Open Questions

None. The existing domain model and persistence contract are sufficient to define the read-by-ID behavior without further product decisions.
