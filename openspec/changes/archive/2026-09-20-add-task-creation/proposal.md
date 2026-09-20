# Proposal

## Why

The service currently exposes only its application baseline and has no persisted task-management capability. Adding task creation establishes the first domain workflow for the Task Manager sample and gives clients a consistent REST contract for creating tasks with lifecycle metadata.

## What Changes

- Add a new task capability under the versioned REST API.
- Accept a task title and description when creating a task.
- Assign a controlled initial status and record creation and last-update timestamps server-side.
- Persist tasks in PostgreSQL through a Flyway-managed schema migration.
- Document the create-task endpoint and request/response/error schemas in the generated OpenAPI document.
- Add unit, architecture, integration, and mutation-test coverage for the new behavior.

## Capabilities

### New Capabilities

- `task-management`: Create and persist tasks with title, description, status, and server-managed timestamps.

### Modified Capabilities

- None.

## Impact

This affects the domain model, application use case, REST adapter, persistence adapter, PostgreSQL schema, Flyway migrations, OpenAPI annotations, and automated tests. The new endpoint is additive under `/api/v1` and does not change the existing baseline endpoint or its response contract. Implementation must preserve the project's Clean Architecture boundaries and use explicit validation and JSON error responses consistent with the existing controller conventions.
