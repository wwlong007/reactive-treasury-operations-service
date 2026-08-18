# ADR-0001: PostgreSQL RLS is the tenant data boundary

- Status: Accepted
- Date: 2026-07-14

## Context

Treasury operators can reuse account and payment references across legal entities. Repository predicates provide useful query intent but are insufficient as the final security boundary: a missing predicate in a new query must not disclose or mutate another entity's money movement data.

The service is reactive and uses pooled R2DBC connections. Payment processing may include asynchronous work and an independent compliance audit while the main operation is still in progress.

## Decision

PostgreSQL row-level security is forced on all tenant-owned tables. The authenticated `tenant_id` is communicated to PostgreSQL through the `app.tenant_id` setting. Policies compare each row to `app_current_tenant()` for both visibility and write checks.

Schema migrations run as `treasury_owner`. Runtime SQL runs as `treasury_app`, which has no ownership, superuser, role creation, or RLS bypass privileges. Application repositories do not add tenant predicates as a substitute for RLS.

The database identity used for a request must continue to represent that request across reactive execution, nested business operations and pooled resource reuse. Missing tenant identity is an authentication failure, never a default tenant selection, and one request must never inherit identity left by another request. At the database boundary this may be enforced by denying access before SQL or by RLS returning no rows and rejecting writes; either outcome is acceptable only when no tenant data is disclosed or changed.

## Consequences

- Integration tests require PostgreSQL; an in-memory database cannot validate the security boundary.
- Cancellation and resource reuse must be included in isolation testing.
- Independent audit work must preserve the request's tenant attribution.
- When outer payment work is suspended around independent audit work, cancellation and pool exhaustion must not strand the outer database resources or affect a later request.
- Public health and API-documentation routes must not be blocked merely because they have no JWT principal. An authenticated JWT with a missing or unusable tenant claim is rejected before tenant business processing.
- Operators can inspect `pg_class`, `pg_policy` and role attributes to verify controls.
