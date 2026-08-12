package com.acme.treasury.web.security;
import com.acme.treasury.application.port.ActorContext;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;
public final class ReactorActorContext implements ActorContext {
    @Override public Mono<String> currentActor() { return ReactiveSecurityContextHolder.getContext().map(it -> it.getAuthentication().getName()).switchIfEmpty(Mono.error(new IllegalStateException("authenticated actor is required"))); }
}

