## Context

See `proposal.md` for the motivation and scope. The repository has no application module yet, so this change establishes the first executable surface while preserving clear boundaries for future task and security capabilities. The runtime target is Java 21 and Spring Boot 4 with Maven, PostgreSQL, Flyway, versioned REST endpoints, generated OpenAPI documentation, and automated architecture and integration checks.

## Goals / Non-Goals

**Goals:**

- Create a runnable Maven application with a clear Clean Architecture dependency direction.
- Provide a small controller/DTO boundary under `/api/v1` plus Actuator health.
- Make PostgreSQL and Flyway startup behavior reproducible locally and in integration tests.
- Make architecture rules, unit tests, Testcontainers integration tests, and Pitest mutation testing part of the build.
- Keep configuration externalized so local Compose and CI/Testcontainers environments use the same application contract.

**Non-Goals:**

- No task domain model, task persistence, task CRUD, users, authentication, or authorization.
- No production deployment manifests or cloud-specific infrastructure.
- No UI, frontend build, or client SDK.

## Decisions

### Layer and package structure

Use separate domain, application/use-case, adapter, and configuration/infrastructure packages. Controllers and DTOs remain at the inbound adapter boundary; application services depend on domain abstractions; persistence implementations and Spring/JPA details remain outbound adapters. ArchUnit rules will assert that inner layers do not depend on outer adapters or framework-specific infrastructure.

This is preferred over a flat controller-service-repository layout because the sample explicitly demonstrates Clean Architecture and future task/security changes need stable extension points. The package names should be conventional and documented in the design implementation tasks rather than encoded into the API contract.

### Minimal REST endpoint

Implement a read-only baseline endpoint at `GET /api/v1` returning a small JSON service/version payload. Use a centralized error representation for framework and controller errors so the initial API contract has a predictable failure shape. Actuator health remains the operational endpoint at `/actuator/health`, while springdoc documents the custom endpoint and its schemas.

This is preferred over exposing only Actuator because it proves the versioned application API, controller wiring, DTO serialization, OpenAPI annotations, and error handling without prematurely inventing task behavior.

### Persistence and migration baseline

Use Spring Data JPA and PostgreSQL configuration with Flyway migrations in `src/main/resources/db/migration`. Add a non-business baseline migration that proves migration wiring on a fresh schema without creating task or user tables. Flyway must run before readiness; migration errors must surface as startup or readiness failures.

This is preferred over an embedded database because PostgreSQL is the target persistence engine and an H2-only baseline could hide dialect and migration issues. A dedicated baseline migration also makes later schema changes explicit and reviewable.

### Local and integration database environments

Provide Docker Compose for a developer-facing PostgreSQL service. Integration tests use Testcontainers PostgreSQL rather than sharing the Compose database, ensuring isolation and reproducibility. Both environments supply connection details through Spring configuration properties or environment variables.

### Dependencies and Spring beans

Add Spring Web, Actuator, validation/error handling support, springdoc OpenAPI, Spring Data JPA, PostgreSQL JDBC, and Flyway runtime dependencies. Add ArchUnit, JUnit 5, Mockito, Testcontainers, and Pitest in test/build configuration as appropriate. Introduce beans only for the application controller/use-case boundary, persistence infrastructure, OpenAPI metadata, and test containers/configuration; avoid adding security beans until authentication is in scope.

### Domain and JPA model

No business domain entity or JPA entity is introduced. The migration baseline must therefore avoid task/user tables and keep future domain schema evolution independent from this foundation.

## Risks / Trade-offs

- **[Spring Boot 4 and dependency compatibility]** → Pin compatible library versions through the Maven dependency management and verify the complete application starts on Java 21.
- **[PostgreSQL unavailable during local startup]** → Document Compose startup and externalized connection settings; keep integration tests self-contained with Testcontainers.
- **[Architecture rules become too restrictive for future features]** → Define only the essential inward dependency rules now and add feature-specific rules with later capabilities.
- **[Actuator or OpenAPI exposure leaks operational detail]** → Expose only the intended health endpoint and document the public surface explicitly; defer broader actuator exposure.
- **[Baseline migration creates accidental business coupling]** → Keep it schema-neutral and require all future business tables to arrive through separate reviewed Flyway scripts.

## Migration Plan

1. Add the Maven application and configuration alongside the currently empty application surface.
2. Add the baseline Flyway migration and verify startup against a fresh PostgreSQL database.
3. Start PostgreSQL through Docker Compose for local verification and run integration tests against isolated Testcontainers PostgreSQL.
4. Verify the generated OpenAPI document and the architecture, unit, integration, and mutation test configuration.
5. Roll back by removing the new application deployment and, if necessary, dropping the foundation-only database schema in a disposable development/test database; no existing production API or business data is affected.
