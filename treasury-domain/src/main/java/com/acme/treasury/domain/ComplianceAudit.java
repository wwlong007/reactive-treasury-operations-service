package com.acme.treasury.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ComplianceAudit(UUID id, TenantId tenantId, String eventType, String aggregateType,
                              UUID aggregateId, String actor, Map<String, String> attributes, Instant occurredAt) {
    public ComplianceAudit { Objects.requireNonNull(id); Objects.requireNonNull(tenantId); Objects.requireNonNull(eventType); Objects.requireNonNull(aggregateType); Objects.requireNonNull(aggregateId); Objects.requireNonNull(actor); attributes = Map.copyOf(attributes); Objects.requireNonNull(occurredAt); }
}

