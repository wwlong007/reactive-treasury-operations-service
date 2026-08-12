package com.acme.treasury.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ApprovalRecord(UUID id, TenantId tenantId, UUID paymentId, String approver,
                             Decision decision, String comment, Instant decidedAt) {
    public enum Decision { APPROVE, REJECT }
    public ApprovalRecord { Objects.requireNonNull(id); Objects.requireNonNull(tenantId); Objects.requireNonNull(paymentId); Objects.requireNonNull(approver); Objects.requireNonNull(decision); Objects.requireNonNull(decidedAt); }
}

