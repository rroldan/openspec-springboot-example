# Proposal

## Why

Clients can currently create a task or retrieve one only when they already know its ID. A paginated and filterable collection endpoint will let clients browse and find tasks without loading an unbounded result set.

## What Changes

- Add `GET /api/v1/tasks` to return tasks in a page envelope, ordered by `createdAt` descending with `id` ascending as a stable tie-breaker by default.
- Support optional single-field sorting on `createdAt` or `title`, with ascending or descending direction.
- Support optional status filtering and case-insensitive text search across title and description.
- Use zero-based `page` and `size` query parameters, defaulting to page `0` and size `20`, with a maximum size of `100`.
- Return standard `400 Bad Request` errors for invalid pagination values or status filters.
- Keep existing create and get-by-ID behavior unchanged; this is an additive API change.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `task-management`: Add a collection retrieval requirement, including filtering, pagination, ordering, and response behavior.

## Impact

- API: Additive `GET /api/v1/tasks` endpoint with documented query parameters and a paged task response in the generated OpenAPI document.
- Code: Affected web controller/response types, application input port and service, output repository port, and Spring Data persistence adapter.
- Persistence: Existing task fields support the requested filters. No schema change is currently expected; if design or query testing shows that indexes are needed, add a Flyway migration under `src/main/resources/db/migration`.
- Compatibility: No breaking changes to existing endpoints or task representations.
