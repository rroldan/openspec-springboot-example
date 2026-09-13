## 1. Workflow Setup

- [x] 1.1 Add a dedicated GitHub Actions workflow under `.github/workflows/` triggered for pull requests and pushes to the repository’s primary development branch, and verify the workflow YAML parses with the intended event filters.
- [x] 1.2 Configure checkout, Java 21 setup, Maven dependency caching, and least-privilege read permissions, then verify the job can initialize on an Ubuntu GitHub-hosted runner.

## 2. Build and Verification Steps

- [x] 2.1 Add an explicit Maven verification step that runs the repository’s test and integration verification lifecycle, and verify failures produce a non-zero workflow step result with diagnostic logs.
- [x] 2.2 Add an explicit Pitest mutation-coverage step using the command documented by the project, and verify the existing Maven mutation threshold remains the source of pass/fail behavior.

## 3. End-to-End Validation

- [x] 3.1 Review the workflow against the `ci-build-verification` scenarios and verify no application code, REST contract, database migration, or runtime dependency changes are introduced.
- [x] 3.2 Run the repository’s available local Maven verification commands where the environment supports them, and verify the workflow invokes the same project-defined checks.
