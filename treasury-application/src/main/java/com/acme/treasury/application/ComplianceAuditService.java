package com.acme.treasury.application;

import com.acme.treasury.application.port.ComplianceAuditRepository;
import com.acme.treasury.application.port.TenantContext;
import com.acme.treasury.domain.ComplianceAudit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.time.Clock;
import java.util.Map;
import java.util.UUID;

@Service
public class ComplianceAuditService {
    private final ComplianceAuditRepository repository;
    private final TenantContext tenantContext;
    private final Clock clock;
    public ComplianceAuditService(ComplianceAuditRepository repository, TenantContext tenantContext, Clock clock) {
        this.repository = repository; this.tenantContext = tenantContext; this.clock = clock;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<ComplianceAudit> record(String eventType, String aggregateType, UUID aggregateId,
                                        String actor, Map<String, String> attributes) {
        return tenantContext.currentTenant().flatMap(tenant -> repository.insert(new ComplianceAudit(
                UUID.randomUUID(), tenant, eventType, aggregateType, aggregateId, actor, attributes, clock.instant())));
    }
}

