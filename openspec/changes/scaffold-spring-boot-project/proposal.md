## Why

The repository currently contains OpenSpec configuration but no executable Spring Boot application structure. Establishing a consistent REST backend scaffold now will give future domain features a clear place for controllers, services, repositories, persistence configuration, security, API documentation, and tests.

## What Changes

- Add a Maven-based Spring Boot application entry point and standard project metadata.
- Establish layered Java packages for versioned REST controllers, services, repositories, domain entities, DTOs, and configuration.
- Add application configuration boundaries for PostgreSQL, JPA, Flyway, Spring Security, and springdoc-openapi.
- Add the resource and test directory layout, including Flyway migration location and unit/integration test separation.
- Define a health-oriented baseline endpoint under `/api/v1` with a documented response contract so the scaffold is executable and verifiable.
- Preserve a REST-only backend scope; no UI or frontend assets are introduced.

## Capabilities

### New Capabilities

- `spring-boot-project-structure`: Defines the executable application scaffold, package boundaries, baseline REST endpoint, configuration conventions, and test layout.

### Modified Capabilities

<!-- No existing capabilities are present in this repository. -->

## Impact

- Adds the Maven build, Java source tree, application resources, and test sources.
- Introduces Spring Boot, Spring Web, Spring Data JPA, PostgreSQL, Flyway, Spring Security, springdoc-openapi, JUnit 5, Mockito, and Testcontainers dependencies.
- Establishes the `/api/v1` public API namespace and OpenAPI generation entry point; this is a new API surface rather than a breaking change.
- Provides the required location for future database migrations under `src/main/resources/db/migration`; the initial scaffold does not require a domain schema migration.
