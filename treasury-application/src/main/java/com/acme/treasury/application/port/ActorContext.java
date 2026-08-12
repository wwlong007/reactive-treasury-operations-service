package com.acme.treasury.application.port;
import reactor.core.publisher.Mono;
public interface ActorContext { Mono<String> currentActor(); }

