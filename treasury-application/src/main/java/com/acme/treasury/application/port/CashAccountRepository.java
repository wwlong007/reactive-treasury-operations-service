package com.acme.treasury.application.port;
import com.acme.treasury.domain.CashAccount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;
public interface CashAccountRepository {
    Mono<CashAccount> findById(UUID id);
    Mono<CashAccount> findByReference(String reference);
    Flux<CashAccount> findAll(int limit, int offset);
    Mono<CashAccount> save(CashAccount account);
}

