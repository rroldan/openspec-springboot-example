# Tasks

## 1. Application Status Update

- [ ] 1.1 Add a dedicated inbound use case and service, register it in `ApplicationConfig`, and verify a focused service test preserves task fields, assigns `updatedAt`, saves the task, and reports an unknown task as not found.

## 2. HTTP Endpoint

- [ ] 2.1 Add a status-only request DTO and `PATCH /api/v1/tasks/{taskId}/status` controller operation, then verify `TaskControllerTest` covers a successful response, missing/null/invalid status, malformed UUID, and unknown task using the standard error shape.

## 3. Persistence and API Documentation

- [ ] 3.1 Add a PostgreSQL integration test proving the status is persisted while identifier, title, description, and `createdAt` are preserved; verify it with `mvn -Dit.test=TaskStatusUpdatePostgresIT verify`.
- [ ] 3.2 Extend `OpenApiSnapshotIT` to verify the PATCH path, required status request enum, and 200/400/404 responses; regenerate `docs/openapi.json` with `mvn -Dopenapi.snapshot.write=true -Dit.test=OpenApiSnapshotIT verify`, then verify the snapshot with `mvn -Dit.test=OpenApiSnapshotIT verify`.
