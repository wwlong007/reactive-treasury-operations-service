package com.acme.treasury.web.security;
import com.acme.treasury.application.port.TenantContext;
import com.acme.treasury.domain.TenantId;
import reactor.core.publisher.Mono;
public final class ReactorTenantContext implements TenantContext {
    public static final Class<TenantId> KEY = TenantId.class;
    @Override public Mono<TenantId> currentTenant() {
        return Mono.deferContextual(context -> context.hasKey(KEY)
                ? Mono.just(context.get(KEY)) : Mono.error(new MissingTenantException()));
    }
}

