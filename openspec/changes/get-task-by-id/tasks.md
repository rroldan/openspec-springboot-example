# Tasks

## 1. API and use case contract

- [x] 1.1 Add a `GetTaskByIdUseCase` contract and a `GetTaskByIdService` implementation that resolves a task by UUID and returns the existing domain model or raises a not-found exception; verify the service test covers the success and missing-task paths.
- [x] 1.2 Add `GET /api/v1/tasks/{taskId}` to `TaskController` and verify the controller test covers `200 OK` and `404 Not Found` responses with the expected JSON payloads.

## 2. Persistence and validation

- [x] 2.1 Extend `TaskRepository` with `Optional<Task> findById(UUID id)` and implement the persistence lookup in `TaskPersistenceAdapter`; verify repository integration tests confirm the record is returned by ID.
- [x] 2.2 Add validation and error mapping for malformed UUIDs and missing task IDs so the API returns consistent `400` and `404` responses; verify the controller or integration tests exercise both cases.

## 3. Documentation and verification

- [x] 3.1 Update the generated OpenAPI snapshot and task response documentation so the new endpoint appears in the committed docs; verify `mvn -Dit.test=OpenApiSnapshotIT verify` passes.
- [x] 3.2 Add or update tests covering successful task lookup, missing task lookup, and malformed UUID input; verify `mvn test -q` passes.
- [x] 3.3 Confirm the task table already supports the lookup without a schema change; if a migration becomes necessary, add the Flyway script under `db/migration` before final verification.
