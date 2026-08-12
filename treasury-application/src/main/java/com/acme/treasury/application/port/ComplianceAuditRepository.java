package com.acme.treasury.application.port;
import com.acme.treasury.domain.ComplianceAudit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;
public interface ComplianceAuditRepository {
    Mono<ComplianceAudit> insert(ComplianceAudit audit);
    Flux<ComplianceAudit> findByAggregate(UUID aggregateId, int limit);
    Flux<ComplianceAudit> findAll(int limit, int offset);
}

