## 1. Project and Architecture Setup

- [x] 1.1 Create the Java 21 Maven Spring Boot 4 project structure, application entry point, package layout, and baseline configuration; verify the project compiles with `mvn test`
- [x] 1.2 Add Clean Architecture dependency rules with ArchUnit 1.5 for domain, application, adapters, and infrastructure; verify forbidden dependency tests fail for an intentional violating fixture or equivalent rule assertion
- [x] 1.3 Add runtime and test dependencies for Web, Actuator, validation/error handling, springdoc-openapi, JPA, PostgreSQL, Flyway, JUnit 5, Mockito, Testcontainers, and Pitest; verify Maven resolves the build and test plugins successfully

## 2. Versioned REST and Documentation

- [x] 2.1 Implement the read-only `GET /api/v1` baseline endpoint with a stable service/version JSON response; verify a controller test returns HTTP 200 and the expected fields
- [x] 2.2 Configure Actuator health exposure and centralized JSON error handling for malformed or unsupported baseline requests; verify 4xx responses contain the documented error identifier, message, and correlation or timestamp metadata
- [x] 2.3 Annotate the baseline endpoint and DTO/error schemas for springdoc-openapi; verify the generated OpenAPI document contains `GET /api/v1`, its 200 response, error response, and the intended health endpoint

## 3. PostgreSQL and Flyway

- [x] 3.1 Add externalized PostgreSQL datasource and Flyway configuration with readiness dependent on successful migrations; verify the application uses configured environment/property values without source changes
- [x] 3.2 Add the foundation-only Flyway migration under `src/main/resources/db/migration` without task or user business tables; verify it applies cleanly to an empty PostgreSQL schema
- [x] 3.3 Add Docker Compose configuration for local PostgreSQL with documented development credentials and connection settings; verify the database service accepts a connection using those settings

## 4. Automated Verification

- [x] 4.1 Add unit tests for the baseline controller/use-case and error behavior using JUnit 5 and Mockito; verify focused unit tests pass
- [x] 4.2 Add Testcontainers PostgreSQL integration tests covering application startup, Flyway migration success, health, and the versioned endpoint; verify the focused integration suite passes against an isolated container
- [x] 4.3 Configure Pitest for the foundation code and document or enforce the selected mutation-testing scope and threshold; verify the mutation test command completes with the configured result
- [x] 4.4 Run the focused Maven verification, generated OpenAPI verification, architecture tests, and a fresh-schema migration check together; verify all foundation requirements are covered before the change is considered complete
