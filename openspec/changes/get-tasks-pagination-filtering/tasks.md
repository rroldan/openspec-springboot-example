# Tasks

## 1. Application Contracts and Use Case

- [x] 1.1 Add application-owned task search criteria (including the constrained sort field/direction) and page result models plus the list-task input/output ports, and verify they compile without Spring Data types in the application boundary
- [x] 1.2 Implement the list-task service with defaults, status/search criteria, and page metadata delegation, and verify unit tests cover defaults and requested values

## 2. Persistence Search

- [x] 2.1 Implement optional status and escaped case-insensitive title/description substring predicates with validated single-field sorting, stable tie-breakers, and page mapping, and verify adapter tests cover combined filters, sorting, and metadata
- [x] 2.2 Add PostgreSQL-backed integration coverage for filtering, ordering, multiple pages, and empty results, and verify the focused integration test passes against Testcontainers
- [x] 2.3 Review the PostgreSQL query plan for the expected search use; if indexing is justified, add a Flyway migration and verify all migrations run on a fresh schema

## 3. REST API

- [ ] 3.1 Add the `GET /api/v1/tasks` endpoint and documented paged response DTO with `status`, `q`, `page`, `size`, and `sort` query parameters, and verify controller tests assert response items and page metadata
- [ ] 3.2 Enforce defaults and parameter bounds and map invalid filters, sort fields/directions, and page values to the shared `INVALID_REQUEST` error response, and verify tests cover invalid inputs and size boundaries

## 4. API Documentation and Verification

- [ ] 4.1 Update the OpenAPI integration assertions and regenerate `docs/openapi.json`, then verify the snapshot includes the list endpoint, filter/pagination/sort parameters, paged response, and standard `400` response
- [ ] 4.2 Run focused unit tests and the relevant PostgreSQL/OpenAPI integration tests, and verify all task-list scenarios pass without regressions to create or get-by-ID behavior
