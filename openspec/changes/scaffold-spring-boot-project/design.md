## Context

See `proposal.md` for the motivation and intended scaffold scope. The repository currently has OpenSpec configuration but no executable application source tree. The design must establish the project's layered Spring Boot conventions while keeping the initial endpoint intentionally small.

## Goals / Non-Goals

**Goals:**

- Create a Maven Spring Boot application with a conventional source/resource/test layout.
- Establish controller, service, repository, domain, DTO, and configuration package boundaries under a single application module.
- Provide a minimal health controller and service path that can be exercised independently of persistence.
- Configure PostgreSQL, JPA, Flyway, Spring Security, and springdoc-openapi integration points without introducing a domain entity or schema.
- Make security protected-by-default, with an explicit public health route.

**Non-Goals:**

- Implementing domain CRUD, authentication flows, user management, or authorization roles beyond the baseline security configuration.
- Introducing database tables, entities, repositories with domain behavior, or an initial Flyway schema migration.
- Building a UI, frontend assets, or a second deployable module.

## Decisions

- **Layered package structure:** Organize application code into controller, service, repository, domain/entity, DTO, and configuration packages. This matches the project's stated architecture and leaves future modules room to maintain DDD-ish boundaries. A single flat package was rejected because it would make ownership and dependency direction ambiguous.
- **Health endpoint through controller and service layers:** Route `GET /api/v1/health` through a controller backed by a small service, returning a DTO rather than a map. This establishes the intended dependency direction and a stable response contract without coupling health behavior to JPA. A controller-only implementation was rejected because it would not demonstrate the scaffold's layer boundaries.
- **Dependency integration points:** Add the Spring Boot starters and libraries required by the project context: web, validation where needed by DTO conventions, data JPA, PostgreSQL runtime, Flyway, security, and springdoc-openapi. Keep configuration in dedicated configuration classes or application properties. No custom Spring beans are introduced beyond the security configuration and health components.
- **Protected-by-default HTTP security:** Configure the health route as explicitly permitted and require authentication for other application routes. This is safer than permitting all routes during scaffolding and preserves the stated role-based security direction. No credentials or production authentication provider are defined by this change.
- **Persistence readiness without schema changes:** Configure datasource/JPA/Flyway boundaries and create the migration directory, but do not add entities or migration scripts until a domain model exists. This avoids an empty or speculative schema migration and keeps fresh-schema behavior deterministic.
- **OpenAPI annotations on the baseline controller/DTO:** Annotate the health endpoint and response model so generated documentation reflects the public route and status field. Runtime API documentation generation is preferred over maintaining a hand-written static contract.

## Risks / Trade-offs

- **[Risk]** Startup may fail when a PostgreSQL datasource is not configured in local development. **Mitigation:** Document configuration through standard Spring properties and keep the health path free of database access; provide test profiles or container-backed integration coverage for environments that require the full application context.
- **[Risk]** Permitting only the health route can make future exploratory endpoints appear unavailable. **Mitigation:** Require each new endpoint to make an explicit security decision and add authorization tests as routes are introduced.
- **[Risk]** Dependency versions can drift from the selected Spring Boot release. **Mitigation:** Use the Spring Boot dependency management/BOM and pin only dependencies not managed by the parent.
- **[Risk]** The initial health response is intentionally narrow and may not represent database or downstream readiness. **Mitigation:** Treat it as a liveness-style baseline; add separate readiness semantics only when operational requirements are defined.
