package com.acme.treasury.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record PaymentInstruction(UUID id, TenantId tenantId, String clientReference, UUID debitAccountId,
                                 String beneficiaryName, String beneficiaryAccount, Money amount,
                                 Status status, int requiredApprovals, int approvalCount,
                                 String createdBy, Instant createdAt, Instant updatedAt, long version) {
    public enum Status { PENDING_RISK, AWAITING_APPROVAL, APPROVED, REJECTED, CANCELLED }
    public PaymentInstruction {
        Objects.requireNonNull(id); Objects.requireNonNull(tenantId); Objects.requireNonNull(clientReference);
        Objects.requireNonNull(debitAccountId); Objects.requireNonNull(beneficiaryName); Objects.requireNonNull(beneficiaryAccount);
        Objects.requireNonNull(amount); Objects.requireNonNull(status); Objects.requireNonNull(createdBy);
        Objects.requireNonNull(createdAt); Objects.requireNonNull(updatedAt);
        if (clientReference.isBlank() || beneficiaryName.isBlank() || beneficiaryAccount.isBlank()) throw new IllegalArgumentException("payment fields must not be blank");
        if (!amount.isPositive()) throw new IllegalArgumentException("payment amount must be positive");
        if (requiredApprovals < 1 || approvalCount < 0 || approvalCount > requiredApprovals) throw new IllegalArgumentException("invalid approval counts");
    }
    public PaymentInstruction riskAccepted(int approvals, Instant now) {
        if (status != Status.PENDING_RISK) throw new DomainConflict("risk decision is not allowed in " + status);
        return copy(Status.AWAITING_APPROVAL, approvals, approvalCount, now, version + 1);
    }
    public PaymentInstruction approve(Instant now) {
        if (status != Status.AWAITING_APPROVAL) throw new DomainConflict("approval is not allowed in " + status);
        int next = approvalCount + 1;
        return copy(next >= requiredApprovals ? Status.APPROVED : status, requiredApprovals, next, now, version + 1);
    }
    public PaymentInstruction reject(Instant now) {
        if (status != Status.AWAITING_APPROVAL) throw new DomainConflict("rejection is not allowed in " + status);
        return copy(Status.REJECTED, requiredApprovals, approvalCount, now, version + 1);
    }
    private PaymentInstruction copy(Status next, int required, int count, Instant now, long nextVersion) {
        return new PaymentInstruction(id, tenantId, clientReference, debitAccountId, beneficiaryName,
                beneficiaryAccount, amount, next, required, count, createdBy, createdAt, now, nextVersion);
    }
}

