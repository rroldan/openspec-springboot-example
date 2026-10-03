# Proposal

## Why

Clients can create, retrieve, and list tasks, but cannot change an existing task. Adding task updates completes the core task-management workflow and allows clients to edit task details and advance its lifecycle.

## What Changes

- Add `PUT /api/v1/tasks/{taskId}` as a full replacement of a task's `title`, `description`, and `status`.
- Keep the task ID and `createdAt` server-managed and unchanged; refresh `updatedAt` on update.
- Return the updated task, use the existing error response conventions for invalid requests and missing tasks, and document the endpoint in OpenAPI.
- No database migration is expected because the existing task schema already stores all updateable fields.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `task-management`: define full task replacement, validation, not-found behavior, timestamps, and the OpenAPI contract.

## Impact

- API: adds a non-breaking `PUT /api/v1/tasks/{taskId}` operation under the existing `/api/v1` prefix.
- Layers: affects the web controller/request DTO, application input port and service, persistence port/adapter, and related tests.
- Persistence: uses the existing task table and fields; no Flyway migration or new dependency is expected.
- API compatibility: additive operation only; existing endpoints and response shapes remain unchanged.
