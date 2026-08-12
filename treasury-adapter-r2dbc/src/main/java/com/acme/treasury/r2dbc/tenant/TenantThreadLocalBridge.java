package com.acme.treasury.r2dbc.tenant;

import com.acme.treasury.domain.TenantId;
import java.util.Optional;

/** Transitional bridge retained while JDBC request-scoped infrastructure is being retired. */
public final class TenantThreadLocalBridge {
    private static final ThreadLocal<TenantId> CURRENT = new ThreadLocal<>();
    private TenantThreadLocalBridge() {}
    public static void set(TenantId tenantId) { CURRENT.set(tenantId); }
    public static Optional<TenantId> current() { return Optional.ofNullable(CURRENT.get()); }
    public static void clear() { CURRENT.remove(); }
}

