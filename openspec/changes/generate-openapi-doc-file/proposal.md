# Proposal

## Why

The application currently exposes its OpenAPI document dynamically through `/v3/api-docs`, but the contract is not available as a reviewable, distributable file in the repository. A checked-in document will make the current REST contract easy to inspect, publish, and compare during API changes.

## What Changes

- Generate a repository OpenAPI document at `docs/openapi.json` from the running application's springdoc output.
- Include the currently documented `/api/v1` baseline, `/api/v1/tasks` task-creation endpoint, `/actuator/health`, component schemas, response codes, and standard error shape.
- Add a repeatable verification step that compares the committed document with the generated application document and detects stale output.
- Keep the runtime `/v3/api-docs` and Swagger UI endpoints unchanged.
- Do not add Flyway migrations or alter persistence, controller behavior, or public API semantics.

## Capabilities

### New Capabilities

<!-- This is a documentation/build artifact change; no new runtime capability is introduced. -->

### Modified Capabilities

<!-- No spec-level requirements change. The change opts out of specs via .openspec.yaml. -->

## Impact

- A new checked-in API contract file under `docs/`.
- Maven/build or verification configuration may be updated to generate and validate the file.
- Existing annotated controllers, DTOs, and `OpenApiConfig` remain the source of truth.
- No database layers, Flyway scripts, Spring beans, endpoint behavior, or compatibility guarantees change; the committed file documents the existing additive API surface.
