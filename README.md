# Task Manager REST foundation

This project is a Java 21 / Spring Boot 4 REST backend using Clean Architecture.
It intentionally contains no task, user, authentication, or authorization features.

## Local PostgreSQL

Start PostgreSQL with:

```bash
docker compose up -d postgres
```

The development database is available at `localhost:5432` with database
`taskmanager`, username `taskmanager`, and password `taskmanager`. The
application reads these values from `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`;
the defaults match the Compose service. Flyway applies migrations before the
application is ready.

## API

- `GET /api/v1` returns the service name and API version.
- `GET /actuator/health` is the operational health endpoint.
- `/v3/api-docs` serves the generated OpenAPI document.
- `/swagger-ui.html` serves the Swagger UI.

Errors use JSON fields `errorId`, `message`, `timestamp`, and `correlationId`.

## Verification

```bash
mvn test
mvn verify                 # Integration tests require a working Docker runtime
mvn org.pitest:pitest-maven:mutationCoverage  # foundation mutation threshold: 50%
```

## OpenAPI snapshot

The committed `docs/openapi.json` is generated from the running application
through the same PostgreSQL Testcontainers setup used by integration tests.
Docker must be running before executing these commands:

```bash
mvn -Dopenapi.snapshot.write=true -Dit.test=OpenApiSnapshotIT verify
mvn -Dit.test=OpenApiSnapshotIT verify
```

The first command regenerates the normalized snapshot. The second command
fails with an actionable stale-snapshot message if the committed file differs
from `/v3/api-docs`.
