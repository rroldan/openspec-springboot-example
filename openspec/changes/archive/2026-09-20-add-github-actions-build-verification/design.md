## Context

The repository is a Maven-based Java 21 Spring Boot service. Its existing `pom.xml` defines unit tests, Failsafe integration verification, ArchUnit checks, Testcontainers-backed integration tests, and a Pitest mutation threshold. The current GitHub Actions configuration is limited to Copilot setup and does not provide a project quality gate. See `proposal.md` for the motivation and `specs/ci-build-verification/spec.md` for the observable requirements.

## Goals / Non-Goals

**Goals:**

- Add one maintainable GitHub Actions workflow that verifies pull requests and pushes to the primary development branch.
- Reuse the commands and thresholds already defined by Maven rather than duplicating project policy in YAML.
- Provision Java 21 and Maven dependency caching on an Ubuntu runner.
- Preserve logs and fail the workflow whenever a required verification command fails.

**Non-Goals:**

- No changes to application layers, Spring beans, REST endpoints, database migrations, or JPA entities.
- No new runtime or Maven dependencies.
- No replacement of the existing Maven test, integration-test, or Pitest configuration.
- No deployment, release, or artifact publishing workflow.

## Decisions

- **Use a dedicated workflow under `.github/workflows/`**: This keeps project verification separate from the existing Copilot setup workflow and makes the quality gate independently visible and maintainable. A modification to the setup workflow was considered, but it would couple agent environment preparation with application validation.
- **Trigger on pull requests and pushes to the primary branch**: Pull-request checks provide pre-merge feedback, while branch pushes protect direct updates and establish a post-merge signal. Path-only filtering was considered, but broad triggers ensure changes to build configuration, tests, and workflow files are not missed.
- **Use `actions/checkout` and `actions/setup-java` with Java 21 and Maven caching**: These are standard GitHub-maintained actions and provide a reproducible runner setup without introducing project dependencies. Installing Java or Maven through ad hoc shell commands was rejected because it is less explicit and less cache-friendly.
- **Run Maven verification and mutation testing as explicit steps**: The workflow will invoke the repository’s existing `mvn verify` and Pitest mutation coverage command so the `pom.xml` remains the source of truth for integration and mutation thresholds. Merging them into one opaque command was rejected because separate steps make failures easier to diagnose.
- **Rely on the GitHub-hosted runner’s Docker capability for Testcontainers**: The project already uses Testcontainers for integration tests, so the workflow will not add a database service or alter application configuration unless the existing tests demonstrate a concrete need. A managed PostgreSQL service was considered but would duplicate the containerized test dependency.

## Risks / Trade-offs

- [Integration tests may be slower or fail if runner Docker behavior differs from local development] → Keep integration verification in the existing `mvn verify` lifecycle and retain full job logs; adjust only if the repository’s established Testcontainers contract requires it.
- [Mutation testing increases CI duration] → Keep it as a distinct required step so the cost is visible and the quality threshold remains enforced rather than silently omitted.
- [The primary branch name may differ from the repository’s configured default] → Confirm the repository’s branch convention while implementing the workflow and use the actual primary branch name in the trigger.
