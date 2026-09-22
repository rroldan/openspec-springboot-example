# Tasks

## 1. Generation and snapshot

- [x] 1.1 Add the repeatable Maven/integration-test generation path that starts the application with the existing PostgreSQL Testcontainers setup, retrieves `/v3/api-docs`, normalizes the JSON, and writes `docs/openapi.json`; verify the file is created from a successful application response.
- [x] 1.2 Add stale-snapshot detection that compares newly generated normalized output with `docs/openapi.json` and fails with an actionable message when they differ; verify the check fails against an intentionally modified snapshot and passes for matching output.
- [x] 1.3 Create the initial `docs/openapi.json` snapshot and verify it is valid JSON with the expected OpenAPI version, metadata, and stable formatting.

## 2. Contract coverage and documentation

- [x] 2.1 Verify the generated OpenAPI docs file reflects the current `/api/v1`, `/api/v1/tasks`, and `/actuator/health` paths, response status codes, request/response schemas, and standard error schema; verify the expected paths and components are present.
- [x] 2.2 Add or update README instructions for generating and validating `docs/openapi.json`, including the Docker/Testcontainers prerequisite; verify the documented command matches the Maven lifecycle.
- [x] 2.3 Confirm no Flyway migration is required because this change does not alter the database schema; verify the existing migration set remains unchanged.

## 3. Tests and final verification

- [x] 3.1 Add focused automated coverage for invalid, empty, and stale OpenAPI output handling; verify the focused Maven test selector passes.
- [x] 3.2 Run the focused OpenAPI generation/verification checks and the existing integration test suite; verify the committed snapshot and runtime `/v3/api-docs` remain consistent without endpoint behavior changes.
