## Why

The project currently documents local Maven verification but does not automatically run those checks for pull requests or pushes. A GitHub Actions workflow will provide consistent, visible feedback for build, unit, integration, and mutation verification before changes are merged.

## What Changes

- Add a GitHub Actions workflow for pull requests and pushes to the main development branch.
- Run the project’s Maven build and test verification on a supported Java 21 environment.
- Run mutation testing as an explicit CI verification step so the configured threshold remains enforced.
- Publish failed checks through standard GitHub Actions job status without changing the REST API or application runtime behavior.

## Capabilities

### New Capabilities

- `ci-build-verification`: Automatically build and verify the Spring Boot project in GitHub Actions using the repository’s Maven verification commands.

### Modified Capabilities

- None.

## Impact

- Adds a workflow under `.github/workflows/` and uses GitHub-hosted Ubuntu runners with Java 21 and Maven.
- Exercises the existing unit, integration, architecture, and mutation-test configuration; integration verification may require the runner’s Docker service for Testcontainers.
- Does not change application code, database migrations, REST endpoints, or public API contracts.
