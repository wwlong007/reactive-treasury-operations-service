package com.acme.treasury.application.port;
import com.acme.treasury.domain.PaymentInstruction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;
public interface PaymentInstructionRepository {
    Mono<PaymentInstruction> insert(PaymentInstruction payment);
    Mono<PaymentInstruction> update(PaymentInstruction payment);
    Mono<PaymentInstruction> findById(UUID id);
    Mono<PaymentInstruction> findByClientReference(String reference);
    Flux<PaymentInstruction> findAll(int limit, int offset);
}

