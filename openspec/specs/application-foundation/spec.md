# application-foundation Specification

## Purpose

Provides a runnable, documented, and testable REST backend foundation on which later task-management and security capabilities can be implemented consistently.

## Requirements

### Requirement: Application exposes a versioned REST baseline

The application SHALL expose a minimal custom REST endpoint under `/api/v1` that returns a stable success response and SHALL expose operational health through the standard health endpoint.

#### Scenario: Versioned baseline endpoint succeeds

- **GIVEN** the application is running
- **WHEN** a client sends `GET /api/v1`
- **THEN** the application returns HTTP `200 OK` with a JSON response identifying the service and API version

#### Scenario: Health endpoint reports application availability

- **GIVEN** the application is running and its required dependencies are available
- **WHEN** a client sends `GET /actuator/health`
- **THEN** the application returns HTTP `200 OK` with a JSON health response

#### Scenario: Invalid baseline request uses the standard error shape

- **GIVEN** the application is running
- **WHEN** a client sends an unsupported method or malformed request to the custom baseline resource
- **THEN** the application returns the appropriate 4xx status and a JSON error body containing a machine-readable error identifier, a human-readable message, and request correlation or timestamp metadata

### Requirement: Baseline API is documented through OpenAPI

The application SHALL publish an OpenAPI document that describes the versioned baseline endpoint, its successful response, its possible error response, and the operational health endpoint when health is part of the exposed API surface.

#### Scenario: OpenAPI document includes baseline endpoint

- **GIVEN** the application is running
- **WHEN** a client retrieves the generated OpenAPI document
- **THEN** the document contains `GET /api/v1` with its response status and JSON schema details

### Requirement: Persistence has a reproducible migration baseline

The application SHALL connect to PostgreSQL using externalized configuration and SHALL apply Flyway migrations from the standard `db/migration` location before serving requests.

#### Scenario: Fresh PostgreSQL schema starts successfully

- **GIVEN** an empty PostgreSQL database with valid connection configuration
- **WHEN** the application starts
- **THEN** Flyway applies the baseline migration successfully and the application becomes ready to serve requests

#### Scenario: Migration failure prevents false readiness

- **GIVEN** a PostgreSQL database whose migration cannot be applied
- **WHEN** the application starts
- **THEN** startup fails or remains unready with an explicit migration error rather than reporting healthy application readiness

### Requirement: Architecture and test boundaries are enforceable

The project SHALL provide automated checks for its Clean Architecture dependency rules and SHALL support unit, integration, and mutation testing using the project-standard test tools.

#### Scenario: Architecture rules reject forbidden dependencies

- **GIVEN** a source change introduces a dependency that violates the declared layer direction
- **WHEN** the architecture test suite runs
- **THEN** the suite fails and identifies the violated architecture rule

#### Scenario: Integration tests run against PostgreSQL

- **GIVEN** a test environment with a container runtime
- **WHEN** the integration test suite runs
- **THEN** it starts PostgreSQL through Testcontainers and verifies application startup and migration behavior against that isolated database

### Requirement: Local development provides PostgreSQL infrastructure

The project SHALL provide Docker Compose configuration for a local PostgreSQL instance with credentials and connection settings that can be supplied to the application without changing source code.

#### Scenario: Developer starts local database

- **GIVEN** Docker Compose is available
- **WHEN** a developer starts the database service using the project-provided Compose configuration
- **THEN** PostgreSQL becomes reachable using the documented default development connection settings
