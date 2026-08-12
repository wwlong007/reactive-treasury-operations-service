package com.acme.treasury.application;
import com.acme.treasury.application.port.ComplianceAuditRepository;
import com.acme.treasury.application.port.TenantContext;
import com.acme.treasury.domain.ComplianceAudit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
@Service
public class AuditQueryService {
    private final ComplianceAuditRepository audits; private final TenantContext tenants;
    public AuditQueryService(ComplianceAuditRepository audits, TenantContext tenants) { this.audits = audits; this.tenants = tenants; }
    @Transactional(readOnly=true) public Flux<ComplianceAudit> list(int limit, int offset) { return tenants.currentTenant().thenMany(audits.findAll(limit, offset)); }
}

