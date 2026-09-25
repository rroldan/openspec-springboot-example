# Proposal

## Why

Task creation is already supported, but clients cannot retrieve a task after it has been persisted. Without a single-task lookup, any workflow that needs to display, validate, or act on an existing task by identifier cannot be implemented cleanly, and the API surface remains incomplete for task management.

## What Changes

- Add a `GET /api/v1/tasks/{taskId}` endpoint to fetch a single persisted task by identifier.
- Return HTTP `200 OK` for a valid lookup, `404 Not Found` when the task is missing, and `400 Bad Request` for malformed identifier input consistent with the application's validation error response.
- Extend the task-management API contract and generated OpenAPI document to describe the new detail endpoint and error behavior.

## Capabilities

### New Capabilities

- None

### Modified Capabilities

- `task-management`: Add the ability to retrieve a single task by identifier and document the corresponding success and error responses.

## Impact

- Controller layer: new task detail endpoint on `TaskController`
- Application layer: new read use case and service for task lookup by ID
- Persistence layer: repository lookup using the existing task table keyed by UUID
- API documentation: generated OpenAPI and snapshot verification update to include the new endpoint
- Compatibility: additive API change only; no schema migration required because the task table already exists
