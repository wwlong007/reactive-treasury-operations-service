package com.acme.treasury.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CashAccount(UUID id, TenantId tenantId, String accountReference, String displayName,
                          Money availableBalance, Money reservedBalance, Status status,
                          long version, Instant createdAt, Instant updatedAt) {
    public enum Status { ACTIVE, SUSPENDED, CLOSED }
    public CashAccount {
        Objects.requireNonNull(id); Objects.requireNonNull(tenantId); Objects.requireNonNull(accountReference);
        Objects.requireNonNull(displayName); Objects.requireNonNull(availableBalance); Objects.requireNonNull(reservedBalance);
        Objects.requireNonNull(status); Objects.requireNonNull(createdAt); Objects.requireNonNull(updatedAt);
        if (accountReference.isBlank()) throw new IllegalArgumentException("accountReference is blank");
        if (!availableBalance.currency().equals(reservedBalance.currency())) throw new IllegalArgumentException("currency mismatch");
    }
    public CashAccount reserve(Money amount, Instant now) {
        if (status != Status.ACTIVE) throw new DomainConflict("account is not active");
        if (!amount.isPositive()) throw new IllegalArgumentException("reservation must be positive");
        if (availableBalance.subtract(amount).isNegative()) throw new DomainConflict("insufficient available balance");
        return new CashAccount(id, tenantId, accountReference, displayName, availableBalance.subtract(amount),
                reservedBalance.add(amount), status, version + 1, createdAt, now);
    }
}

