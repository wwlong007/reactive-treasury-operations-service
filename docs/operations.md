# Operations runbook

## Service indicators

Use the readiness probe for traffic routing and the liveness probe only for process replacement. Prometheus metrics are exposed at `/actuator/prometheus`. Alert on sustained HTTP 5xx rates, R2DBC acquisition latency, pool exhaustion, transaction rollback rate, and PostgreSQL policy or permission errors.

Logs are emitted as structured JSON. Correlate incidents with the HTTP trace identifier, authenticated subject and payment identifier. Tenant identifiers may be logged as structured security metadata; beneficiary account values and JWTs must never be logged.

## Database access

Production uses two credentials:

- `treasury_owner` applies versioned Flyway migrations during deployment.
- `treasury_app` serves application traffic and has no DDL or RLS bypass privilege.

Verify controls after a database restore:

```sql
select rolname, rolsuper, rolbypassrls from pg_roles where rolname in ('treasury_owner','treasury_app');
select relname, relrowsecurity, relforcerowsecurity from pg_class
where relname in ('cash_account','payment_instruction','approval_record','compliance_audit');
```

Never grant table ownership, `BYPASSRLS`, or superuser to the runtime role. Do not disable the connection pool to address tenant-isolation symptoms.

## Isolation incident response

If a response appears to contain another tenant's data, remove the affected instance from rotation and retain application and PostgreSQL logs. Record the request trace, JWT issuer and subject, tenant claim, transaction outcome and pool metrics. Validate policies and role attributes before replaying with synthetic data in an isolated environment. Credential rotation alone does not prove that request isolation has been restored.

## Deployment and rollback

Deploy migrations before replacing application instances. Migrations are forward-only; restore service code to the previous image only when its schema compatibility is documented. Graceful shutdown must be allowed to finish active reactive transactions before the process is terminated.
