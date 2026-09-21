# Design

## Context

The REST contract is generated at runtime by springdoc from `ApplicationController`, `TaskController`, their DTOs, error models, and `OpenApiConfig`. The application already exposes the JSON document at `/v3/api-docs`, and `FoundationIntegrationIT` verifies selected task-creation entries. See `proposal.md` for the motivation.

## Goals / Non-Goals

**Goals:**

- Keep a deterministic, reviewable snapshot at `docs/openapi.json`.
- Generate the snapshot from the running application output rather than maintaining a second hand-written contract.
- Verify that the snapshot is valid OpenAPI JSON and contains the public endpoints and schemas currently exposed by the service.
- Reuse the existing springdoc dependency and integration-test conventions.

**Non-Goals:**

- No new REST endpoints, response semantics, authentication behavior, persistence changes, or Flyway migrations.
- No replacement of `/v3/api-docs` or `/swagger-ui.html`.
- No attempt to make the committed snapshot a separate runtime source of truth.

## Decisions

### Use JSON from the existing `/v3/api-docs` endpoint

The committed file will use JSON because springdoc already emits JSON at the configured endpoint. This avoids adding a YAML serializer or a second OpenAPI-generation implementation. A YAML artifact was considered, but would require conversion tooling or a new runtime/build dependency without improving the generated contract.

### Generate through a repeatable Maven verification path

Add a focused generation/verification mechanism that starts the application in the same integration-test environment used by `FoundationIntegrationIT`, retrieves `/v3/api-docs`, and writes or compares `docs/openapi.json`. The implementation should make stale-output failures explicit and avoid silently accepting a missing or empty document. A standalone shell-only approach was considered, but would duplicate database/container setup and be less portable than the existing Maven test lifecycle.

### Treat annotations and configuration as the source of truth

No controller, DTO, service, repository, entity, or OpenAPI bean behavior changes are required. The generator consumes the document produced from the existing annotations and `OpenApiConfig`; future API changes update the snapshot through the same verification path.

## Risks / Trade-offs

- **[Committed snapshot becomes stale]** → Run snapshot verification in the focused Maven checks and fail when the generated content differs.
- **[Integration generation depends on PostgreSQL/Testcontainers]** → Reuse the existing Testcontainers setup and document the Docker requirement for the generation command.
- **[OpenAPI output contains nondeterministic ordering or metadata]** → Normalize JSON before comparison and write a stable formatted representation.
- **[A generated document accidentally exposes internal endpoints]** → Assert the expected public paths and keep actuator exposure constrained by the existing configuration.

## Migration Plan

1. Add the generation/verification support and create the initial `docs/openapi.json` from the current application.
2. Run focused Maven tests and inspect the generated file for the baseline, task-creation, health, schemas, and error responses.
3. Roll back by removing the snapshot and verification wiring; runtime springdoc endpoints remain unchanged.
