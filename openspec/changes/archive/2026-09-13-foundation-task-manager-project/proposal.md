## Why

The repository currently contains only OpenSpec configuration, so there is no runnable backend baseline for the Task Manager sample. Establishing the executable foundation now gives future task and security changes a consistent Spring Boot, Clean Architecture, persistence, API, and testing surface.

## What Changes

- Create a Java 21 Maven Spring Boot 4 REST application skeleton.
- Establish Clean Architecture package boundaries and enforce them with ArchUnit 1.5.
- Add a minimal custom versioned endpoint under `/api/v1` and expose Actuator health.
- Configure springdoc-openapi so the baseline endpoints are documented.
- Configure PostgreSQL persistence and Flyway migrations, including a baseline migration under `db/migration`.
- Add Docker Compose support for local PostgreSQL development.
- Add JUnit 5, Mockito, Testcontainers, and Pitest test infrastructure.
- Define a consistent baseline error response for the custom REST endpoint.
- No task-management business operations, users, authentication, or authorization are introduced by this change.

## Capabilities

### New Capabilities

- `application-foundation`: Provides the runnable REST backend baseline, versioned health/readiness endpoint, API documentation, persistence configuration, local database setup, and architecture/test constraints.

### Modified Capabilities

None.

## Impact

- Adds the Maven project structure, Spring configuration, controllers/DTOs, architecture tests, and test configuration across the Clean Architecture layers.
- Adds PostgreSQL and Flyway runtime dependencies plus OpenAPI, Actuator, Testcontainers, and Pitest build/test configuration.
- Adds a Docker Compose development service and a baseline Flyway migration under `src/main/resources/db/migration`.
- Introduces the initial public `/api/v1` endpoint and its generated OpenAPI contract; this is additive and has no breaking change to an existing API.
