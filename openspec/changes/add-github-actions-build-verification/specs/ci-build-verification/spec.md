## Purpose

Provides an automated GitHub Actions quality gate that verifies the Spring Boot service builds and meets its existing automated-test and mutation-testing expectations before changes are integrated.

## ADDED Requirements

### Requirement: CI SHALL verify the Maven project on repository changes

The repository SHALL run its Maven verification checks in GitHub Actions for pull requests and pushes to the primary development branch, using a supported Java 21 environment.

#### Scenario: Pull request passes project verification

- **GIVEN** a pull request targets the primary development branch
- **WHEN** the GitHub Actions workflow runs
- **THEN** it SHALL execute the project’s Maven test and verification commands
- **AND** the workflow check SHALL pass only if all required commands exit successfully

#### Scenario: Push receives the same verification

- **GIVEN** a commit is pushed to the primary development branch
- **WHEN** the GitHub Actions workflow runs
- **THEN** it SHALL execute the same required verification commands as the pull-request workflow
- **AND** the workflow SHALL report a failed check when any required command fails

### Requirement: CI SHALL enforce mutation-test verification

The workflow SHALL run the repository-configured mutation test command and SHALL preserve the mutation threshold configured by the Maven project.

#### Scenario: Mutation threshold is met

- **GIVEN** the mutation test command reaches the configured threshold
- **WHEN** mutation testing completes
- **THEN** the mutation verification step SHALL exit successfully
- **AND** the overall workflow SHALL remain eligible to pass

#### Scenario: Mutation threshold is not met

- **GIVEN** mutation testing reports a score below the configured threshold or otherwise exits unsuccessfully
- **WHEN** the mutation verification step completes
- **THEN** the workflow SHALL fail
- **AND** GitHub Actions SHALL expose the failed job status to the triggering pull request or branch

### Requirement: CI SHALL provide reproducible and diagnosable job execution

The workflow SHALL check out the repository, provision Java 21, use Maven dependency caching where supported, and expose command failures in the job log without changing application runtime configuration.

#### Scenario: Verification command fails

- **GIVEN** a Maven verification command exits with a non-zero status
- **WHEN** the workflow processes the command result
- **THEN** the corresponding workflow step SHALL fail
- **AND** the job log SHALL retain the command output needed to diagnose the failure
