## 1. Maven and project structure

- [ ] 1.1 Create the Maven project descriptor, Spring Boot application entry point, and standard `src/main/java`, `src/main/resources`, and `src/test/java` structure; verify `mvn test` can resolve and compile the scaffold.
- [ ] 1.2 Add the layered package boundaries for controllers, services, repositories, domain/entities, DTOs, and configuration; verify the expected package directories and application context structure are present.
- [ ] 1.3 Add application resource configuration for PostgreSQL, JPA, Flyway, and springdoc-openapi using standard Spring properties; verify the application can load configuration with a test profile.
- [ ] 1.4 Add the `src/main/resources/db/migration` directory and document that no domain schema migration is needed for the initial scaffold; verify Flyway starts cleanly against a fresh schema without an application migration.

## 2. Baseline API and security

- [ ] 2.1 Implement the health service, response DTO, and versioned controller for `GET /api/v1/health`; verify an unauthenticated request returns HTTP 200 and JSON containing `"status": "UP"`.
- [ ] 2.2 Add protected-by-default Spring Security configuration with an explicit permit rule for `/api/v1/health`; verify an unauthenticated request to another application route receives the configured authentication-required response.
- [ ] 2.3 Annotate the health endpoint and response DTO for generated OpenAPI documentation; verify the generated OpenAPI document contains `GET /api/v1/health`, its 200 response, and the status property.

## 3. Tests and integration validation

- [ ] 3.1 Add unit tests for the health service and controller response contract using JUnit 5 and Mockito; verify the focused unit test suite passes.
- [ ] 3.2 Add Spring context/security integration coverage, using Testcontainers where a real PostgreSQL-backed context is required; verify startup, public health access, protected application routes, and Flyway behavior on a fresh database.
- [ ] 3.3 Run the focused Maven tests and then the complete Maven verification lifecycle; verify the scaffold compiles, tests pass, and generated API documentation remains consistent with the specification.
