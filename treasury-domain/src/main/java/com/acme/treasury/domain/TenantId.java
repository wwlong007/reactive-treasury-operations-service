package com.acme.treasury.domain;

import java.util.Objects;
import java.util.UUID;

public record TenantId(UUID value) {
    public TenantId { Objects.requireNonNull(value, "value"); }
    public static TenantId parse(String raw) {
        try { return new TenantId(UUID.fromString(Objects.requireNonNull(raw, "raw"))); }
        catch (IllegalArgumentException ex) { throw new IllegalArgumentException("tenant_id must be a UUID", ex); }
    }
    @Override public String toString() { return value.toString(); }
}

