# Tasks

## 1. Application and Persistence

- [x] 1.1 Add the delete input port and application service, wire the service in `ApplicationConfig`, and test that existing tasks are deleted while missing tasks raise `TaskNotFoundException`.
- [x] 1.2 Extend `TaskRepository` and `TaskPersistenceAdapter` with deletion, and verify the adapter delegates deletion to Spring Data JPA in its focused unit tests.
- [x] 1.3 Add a PostgreSQL integration test that deletes a persisted task through the application use case and verifies the row is absent afterward.

## 2. REST Endpoint and OpenAPI

- [x] 2.1 Add `DELETE /api/v1/tasks/{taskId}` to `TaskController`, returning an empty `204` response, and add controller tests for success, `404 TASK_NOT_FOUND`, and malformed-UUID `400 INVALID_REQUEST`.
- [x] 2.2 Document the DELETE operation's `204`, `400`, and `404` responses and update `docs/openapi.json`; verify the generated OpenAPI snapshot test checks the operation and responses.

## 3. Verification

- [x] 3.1 Run the focused controller, application-service, persistence-adapter, PostgreSQL integration, and OpenAPI snapshot tests; verify they pass and existing task endpoint behavior remains unchanged.
