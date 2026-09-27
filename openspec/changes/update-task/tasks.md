# Tasks

## 1. Application Update Flow

- [ ] 1.1 Add the update input port and service that load the existing task, preserve its ID and `createdAt`, apply the request fields, assign `updatedAt` from the injected clock, and save; verify valid updates and missing-task behavior with service tests.
- [ ] 1.2 Wire the update use case into application configuration and expose the PUT route through the task controller; verify a valid replacement returns HTTP `200 OK` with the saved task.

## 2. Request Validation and API Contract

- [ ] 2.1 Add a validated replacement request containing title, description, and status, document the PUT operation in OpenAPI, and add controller error tests; verify blank/missing/invalid fields and malformed IDs return HTTP `400` with the standard error shape, and unknown IDs return HTTP `404`.
- [ ] 2.2 Add persistence integration coverage showing an update is durable and preserves the task ID and `createdAt` while saving the replacement fields and server-assigned `updatedAt`; verify with the focused PostgreSQL integration test.

## 3. API Documentation and Verification

- [ ] 3.1 Regenerate `docs/openapi.json` using the existing snapshot write mode and verify it documents the required PUT request fields and the `200`, `400`, and `404` responses.
- [ ] 3.2 Run the focused update tests and the full Maven test suite, including architecture and OpenAPI snapshot checks; verify all pass.
