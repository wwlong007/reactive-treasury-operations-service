package com.acme.treasury.application.port;
import com.acme.treasury.domain.ApprovalRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;
public interface ApprovalRepository {
    Mono<ApprovalRecord> insert(ApprovalRecord approval);
    Mono<Boolean> exists(UUID paymentId, String approver);
    Flux<ApprovalRecord> findByPaymentId(UUID paymentId);
}

