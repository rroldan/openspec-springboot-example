# Design

## Context

See proposal.md for motivation. The existing `/api/v1/tasks` controller handles task creation and ID lookup. The application input and output ports currently support only those operations; the Spring Data repository already uses JPA, and the task table contains status, title, description, and creation timestamp fields. The API uses a shared exception handler and generated OpenAPI documentation.

## Goals / Non-Goals

**Goals:**
- Keep pagination and filtering within the existing Clean Architecture boundaries.
- Reuse the current task representation and standard request-error response.
- Keep result ordering deterministic across page requests.
- Allow a constrained, documented client sort without exposing arbitrary persistence properties.

**Non-Goals:**
- Add task mutation or deletion operations.
- Change the task entity, table schema, or existing endpoint contracts.
- Add multi-column client-selectable sorting or new dependencies.

## Decisions

- **Add a collection use case and application-owned query/page models.** The input port will accept the optional status and search text plus page and size, and return task items with total-count metadata. The output port will expose a matching search operation. This keeps Spring Data `Page` and `Pageable` types out of the application boundary; using those framework types in the use case was rejected because it would couple the application layer to persistence.
- **Build the filter query in the persistence adapter.** Extend the existing Spring Data repository with `JpaSpecificationExecutor` and compose optional status and case-insensitive title/description substring predicates. Escape SQL `LIKE` metacharacters in user text so the specified substring search treats them literally. Map the returned page into application-owned page data. A derived repository method for every filter combination was rejected because the combinations grow with each optional filter.
- **Validate and translate query parameters at the web boundary.** Bind `status` to the existing task status enum and validate page as non-negative and size from 1 through 100, with defaults from the spec. Reuse the shared error handler's `INVALID_REQUEST` `ApiError` for malformed or out-of-range values. Expose a page response DTO with the documented item and metadata fields rather than serializing Spring Data's page implementation.
- **Apply deterministic sorting in the use case/persistence request.** Use `createdAt` descending and `id` ascending when the client omits sort. For a requested sort, use its validated field and direction, then add `id` ascending as a tie-breaker unless sorting by `id` (which is not an exposed sort field).
- **Allow one validated sort field and direction.** Accept `createdAt` or `title` with `asc` or `desc`, defaulting direction to ascending when omitted. If no sort is supplied, keep the default `createdAt` descending order; append `id` ascending as a deterministic tie-breaker. Reject unsupported fields or directions through the standard `INVALID_REQUEST` error. A single sort key keeps the API simple and prevents arbitrary database-property ordering; repeated sort parameters are out of scope.
- **Do not change the schema or domain model.** These filters use existing persisted columns and the endpoint needs no new Spring bean or external dependency. Add a Flyway migration only if query-plan or performance evidence warrants an index; a substring search over large tables may otherwise become expensive.
- **Update the OpenAPI snapshot with the endpoint contract.** Annotate the endpoint and response DTO so springdoc describes all query parameters, success shape, and standard validation error; regenerate and verify `docs/openapi.json` using the repository's snapshot mechanism.

## Risks / Trade-offs

- [Substring search may scan many task rows as the table grows] → Verify the query against PostgreSQL and review query plans; add a targeted Flyway index only if measured scale warrants it.
- [Changing filters, sort keys, or page boundaries during implementation could yield subtly inconsistent page metadata] → Cover individual and combined filters and sorting, boundary validation, empty results, and multiple pages in service and PostgreSQL-backed integration tests.
- [The shared error handler intentionally returns a generic validation message] → Use its established `INVALID_REQUEST` shape rather than leaking enum parsing or query details.

## Migration Plan

No data migration is expected. Deploy the additive endpoint and its application/persistence support; rollback by reverting that code. If performance testing leads to an index migration, make it additive and retain compatibility with the prior application version before deployment.
