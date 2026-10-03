# Proposal

## Why

Clients can currently create, retrieve, list, and replace tasks, but cannot remove a task through the REST API. Adding deletion completes the task lifecycle and allows clients to remove tasks they no longer need.

## What Changes

- Add `DELETE /api/v1/tasks/{taskId}` to remove an existing task.
- Return HTTP `204 No Content` when deletion succeeds; return the standard `TASK_NOT_FOUND` error with HTTP `404` for an unknown valid UUID and the standard `INVALID_REQUEST` error with HTTP `400` for a malformed UUID.
- Document the endpoint and its responses in the generated OpenAPI document.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `task-management`: Add task deletion behavior and its HTTP, error, and API documentation contract.

## Impact

The change affects the task web controller, application input/use-case and output/repository ports, persistence adapter, and corresponding unit and integration tests. It adds a public API operation but does not alter existing operations or their contracts. Deletion uses the existing task table and requires no Flyway migration or new dependencies. The generated OpenAPI document will gain the DELETE operation and its success and error responses.
