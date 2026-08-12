package com.acme.treasury.application;
import com.acme.treasury.application.port.CashAccountRepository;
import com.acme.treasury.application.port.TenantContext;
import com.acme.treasury.domain.CashAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;
@Service
public class AccountQueryService {
    private final CashAccountRepository accounts; private final TenantContext tenants;
    public AccountQueryService(CashAccountRepository accounts, TenantContext tenants) { this.accounts = accounts; this.tenants = tenants; }
    @Transactional(readOnly=true) public Mono<CashAccount> get(UUID id) { return tenants.currentTenant().then(accounts.findById(id)); }
    @Transactional(readOnly=true) public Flux<CashAccount> list(int limit, int offset) { return tenants.currentTenant().thenMany(accounts.findAll(limit, offset)); }
}

