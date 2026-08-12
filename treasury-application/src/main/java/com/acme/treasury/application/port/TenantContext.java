package com.acme.treasury.application.port;
import com.acme.treasury.domain.TenantId;
import reactor.core.publisher.Mono;
public interface TenantContext { Mono<TenantId> currentTenant(); }

