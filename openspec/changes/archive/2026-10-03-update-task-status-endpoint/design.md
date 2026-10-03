# Design

## Context

The task REST controller is rooted at `/api/v1/tasks`. Full replacement currently uses a dedicated inbound use case and service, while `TaskRepository` already provides `findById` and `save`. `Task` is immutable and carries `createdAt` and `updatedAt`; `TaskStatus` defines `TODO`, `IN_PROGRESS`, and `DONE`. See proposal.md for motivation and the task-management spec delta for the behavior contract.

## Goals / Non-Goals

**Goals:**

- Follow the existing clean-architecture flow from a web request through an inbound use case to the task repository.
- Preserve all task fields other than status and the server-managed `updatedAt`.
- Keep the endpoint additive without introducing persistence schema changes.

**Non-Goals:**

- Change the existing PUT replacement operation or task response shape.
- Add status transition rules, audit history, or authorization policy changes.
- Change task persistence or add a database migration.

## Decisions

- Add a dedicated inbound use case and service for status changes rather than reusing the full-replacement use case. Reusing it would require resending title and description and could accidentally overwrite them, while a focused operation makes the update boundary explicit.
- Add a request DTO containing only a non-null `TaskStatus`, and expose the PATCH route on the existing task controller. This fits the project's enum validation and controller conventions and documents the status enum in OpenAPI.
- The application service will load the existing task, construct a replacement preserving its identifier, title, description, and `createdAt`, assign the submitted status and current server time as `updatedAt`, then save it through the existing `TaskRepository`. No new repository method, Spring dependency, or bean category is needed; register the use case through `ApplicationConfig` with the existing UTC clock convention.
- Support transitions between any of the existing enum values. The domain currently defines no transition policy, so adding one would be a separate behavioral decision.
- Preserve the API's established error mapping: malformed UUID or request data maps to `400 INVALID_REQUEST`; a missing task maps to `404 TASK_NOT_FOUND`; success returns the existing `TaskResponse`.
- The generated OpenAPI snapshot at `docs/openapi.json` is maintained by `OpenApiSnapshotIT`; regenerate and review it when the endpoint and request schema are added.

## Risks / Trade-offs

- [A status request may fail validation before the task is looked up] → Keep input validation and error behavior consistent with the existing API; malformed or incomplete requests use the standard 400 response.
- [The status endpoint and PUT operation both modify status] → Document their separate intent: PATCH changes status only, while PUT continues replacing title, description, and status.

## Migration Plan

No database migration is required because the existing `tasks.status` column already stores the supported enum values. Deploy the additive API change and updated OpenAPI snapshot together. Rollback can remove the new endpoint and its application wiring without data rollback.
