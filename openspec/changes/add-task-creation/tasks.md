# Tasks

## 1. Persistence and Domain Foundations

- [ ] 1.1 Add the task domain model and `TODO`/`IN_PROGRESS`/`DONE` status representation, then verify the model enforces required title and server-managed lifecycle fields through unit tests
- [ ] 1.2 Add the Flyway migration for the `tasks` table with identifier, title, description, constrained status, and timestamp columns, then verify it applies cleanly to a fresh PostgreSQL schema
- [ ] 1.3 Add the JPA entity, Spring Data repository, and persistence adapter behind the application output port, then verify the adapter maps all task fields correctly in a focused persistence test

## 2. Application and REST API

- [ ] 2.1 Add the create-task input port and application service, assigning status `TODO` and server timestamps while ignoring client lifecycle fields; verify valid and invalid inputs with unit tests
- [ ] 2.2 Add request and response DTOs plus `POST /api/v1/tasks`, returning HTTP `201 Created` for valid requests and the existing JSON error shape with HTTP `400 Bad Request` for blank or missing titles; verify controller behavior with MockMvc tests
- [ ] 2.3 Add OpenAPI annotations and schemas for the create request, task response, and validation error response, then verify the generated document contains `POST /api/v1/tasks`, its `201` and `400` responses, and the documented schemas

## 3. Architecture and Integration Verification

- [ ] 3.1 Extend the architecture tests for task domain, application, adapter, and infrastructure dependency directions, then verify the architecture suite passes and rejects forbidden dependencies
- [ ] 3.2 Add a Testcontainers PostgreSQL integration test covering Flyway startup, task creation, persisted status, and timestamps, then verify the endpoint succeeds against a fresh isolated database
- [ ] 3.3 Run focused Maven tests and mutation testing for task creation, then verify validation, initial status, timestamp assignment, persistence, and API mapping meet the configured thresholds
- [ ] 3.4 Run the complete project verification and inspect the generated OpenAPI output, then verify existing application-foundation behavior remains unchanged
