# Proposal

## Why

Clients currently have to submit a full task replacement to change its lifecycle status, even when the title and description should remain untouched. A focused status endpoint gives clients a smaller, clearer operation for advancing or correcting task state.

## What Changes

- Add `PATCH /api/v1/tasks/{taskId}/status` to update only a task's lifecycle status from a JSON request.
- Return the updated task representation and use the existing standard error responses for invalid requests, malformed identifiers, and unknown tasks.
- Document the endpoint and status request in the generated OpenAPI document.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `task-management`: Add a status-only task update operation and its request, response, validation, and error behavior.

## Impact

- Affects the task web controller, an inbound application use case/service, domain task update handling, and their tests while preserving existing task fields and server-managed timestamps.
- Adds an API surface under `/api/v1`; existing endpoints remain unchanged. No breaking change is intended.
- Updates the generated `docs/openapi.json` snapshot. No database migration is expected because task status is already persisted.
