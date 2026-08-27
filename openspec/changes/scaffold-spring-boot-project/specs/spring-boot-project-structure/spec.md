## Purpose

This capability defines a runnable, REST-only Spring Boot foundation with stable package boundaries, baseline operational behavior, and conventions that support future domain features.

## ADDED Requirements

### Requirement: Executable Spring Boot service
The system SHALL start as a Maven-built Spring Boot application using the project's supported Java and Spring Boot versions, and SHALL expose its REST API under the `/api/v1` namespace.

#### Scenario: Application starts with valid configuration
- **GIVEN** the required runtime configuration for the application is available
- **WHEN** the service is started
- **THEN** the application SHALL complete startup successfully and listen for HTTP requests

#### Scenario: REST API uses versioned namespace
- **WHEN** a public API endpoint is exposed by the service
- **THEN** its route SHALL be under `/api/v1`

### Requirement: Baseline health endpoint
The system SHALL provide an unauthenticated health-oriented endpoint at `GET /api/v1/health` that returns a JSON response containing the service status.

#### Scenario: Health check succeeds
- **WHEN** a client sends `GET /api/v1/health`
- **THEN** the service SHALL return HTTP `200 OK` with a JSON body containing `"status": "UP"`

#### Scenario: Health response is documented
- **WHEN** the OpenAPI document is generated
- **THEN** it SHALL describe `GET /api/v1/health`, its HTTP `200` response, and the JSON status property

### Requirement: Protected-by-default security baseline
The system SHALL require authentication for application endpoints by default while allowing the baseline health endpoint to be accessed without authentication.

#### Scenario: Health endpoint is public
- **WHEN** an unauthenticated client sends `GET /api/v1/health`
- **THEN** the service SHALL return the health response rather than an authentication challenge

#### Scenario: Other application routes require authentication
- **WHEN** an unauthenticated client requests an application route other than the public health endpoint
- **THEN** the service SHALL reject the request with the configured authentication-required HTTP response

### Requirement: Standard project resource and test boundaries
The project SHALL provide conventional locations for application resources, database migrations, unit tests, and integration tests so future features can be added without changing the scaffold's structure.

#### Scenario: Migration location is available
- **WHEN** a database migration is added
- **THEN** it SHALL be discoverable under `src/main/resources/db/migration` by the configured migration system

#### Scenario: Test layers are separated
- **WHEN** unit or integration tests are added
- **THEN** unit tests SHALL be placeable in the standard test source tree and integration tests SHALL have a distinct, conventional location or naming boundary
