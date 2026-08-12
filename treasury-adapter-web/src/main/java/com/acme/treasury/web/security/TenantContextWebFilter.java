package com.acme.treasury.web.security;

import com.acme.treasury.domain.TenantId;
import com.acme.treasury.r2dbc.tenant.TenantThreadLocalBridge;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public final class TenantContextWebFilter implements WebFilter {
    @Override public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal().cast(JwtAuthenticationToken.class).flatMap(authentication -> {
            var raw = authentication.getToken().getClaimAsString("tenant_id");
            var tenant = TenantId.parse(raw);
            TenantThreadLocalBridge.set(tenant);
            return chain.filter(exchange).contextWrite(context -> context.put(ReactorTenantContext.KEY, tenant))
                    .doFinally(signal -> TenantThreadLocalBridge.clear());
        });
    }
}

