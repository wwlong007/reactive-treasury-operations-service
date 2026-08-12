# Reactive Treasury Control Service

Reactive Treasury Control Service is the payment-control boundary used by a corporate treasury platform. It owns cash-account availability, payment instructions, maker/checker approvals, risk decisions, balance reservations, and the compliance audit trail. Tenant isolation is enforced by PostgreSQL row-level security in addition to application authentication.

## Architecture

The service follows ports and adapters across five Maven modules:

| Module | Responsibility |
| --- | --- |
| `treasury-domain` | Money, accounts, payment and approval state transitions |
| `treasury-application` | Transactional use cases and inbound/outbound ports |
| `treasury-adapter-r2dbc` | PostgreSQL repositories, pool and RLS integration |
| `treasury-adapter-web` | JWT security, REST resources and Problem Details |
| `treasury-boot` | Runtime composition, Flyway, health and metrics |

Java 17 and Maven 3.9.11 are the supported build toolchain. The runtime uses Spring Boot 3.5, WebFlux, R2DBC PostgreSQL and PostgreSQL 16.

## Local development

Start PostgreSQL, then run the application:

```bash
docker compose up -d postgres
./mvnw verify
./mvnw -pl treasury-boot -am spring-boot:run
```

The database bootstrap creates separate `treasury_owner` and `treasury_app` roles. Flyway runs as the owner; all reactive business traffic uses the restricted application role. A local OpenID Connect issuer must expose JWTs whose `tenant_id` claim is a UUID.

Useful endpoints:

- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`
- `GET /v3/api-docs`
- `POST /api/v1/payments`
- `GET /api/v1/payments`
- `POST /api/v1/payments/{id}/decisions`
- `GET /api/v1/accounts`
- `GET /api/v1/audits`

See [the operations runbook](docs/operations.md) for production diagnostics and [ADR-0001](docs/adr/0001-postgresql-rls.md) for the isolation decision.

## Security model

Every request must be authenticated. The JWT subject identifies the actor and `tenant_id` selects the tenant. Four business tables have forced RLS with symmetric `USING` and `WITH CHECK` policies. The application role is deliberately neither a superuser nor a `BYPASSRLS` role. Do not weaken database policies to work around application integration problems.

## API compatibility

The `/api/v1` representation, status codes, JWT claim name, database schema and payment state machine are compatibility boundaries. Changes require an ADR and a versioned migration.

