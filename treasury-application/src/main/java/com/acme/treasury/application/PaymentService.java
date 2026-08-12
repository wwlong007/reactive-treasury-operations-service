package com.acme.treasury.application;

import com.acme.treasury.application.port.*;
import com.acme.treasury.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Clock;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {
    private final CashAccountRepository accounts;
    private final PaymentInstructionRepository payments;
    private final ApprovalRepository approvals;
    private final RiskAssessmentPort risk;
    private final TenantContext tenants;
    private final ActorContext actors;
    private final ComplianceAuditService audit;
    private final Clock clock;

    public PaymentService(CashAccountRepository accounts, PaymentInstructionRepository payments,
                          ApprovalRepository approvals, RiskAssessmentPort risk, TenantContext tenants,
                          ActorContext actors, ComplianceAuditService audit, Clock clock) {
        this.accounts = accounts; this.payments = payments; this.approvals = approvals; this.risk = risk;
        this.tenants = tenants; this.actors = actors; this.audit = audit; this.clock = clock;
    }

    @Transactional
    public Mono<PaymentInstruction> create(PaymentCommands.Create command) {
        return Mono.zip(tenants.currentTenant(), actors.currentActor(), accounts.findByReference(command.debitAccountReference())
                        .switchIfEmpty(Mono.error(new AccountNotFound(command.debitAccountReference()))))
                .flatMap(tuple -> {
                    var now = clock.instant();
                    var payment = new PaymentInstruction(UUID.randomUUID(), tuple.getT1(), command.clientReference(), tuple.getT3().id(),
                            command.beneficiaryName(), command.beneficiaryAccount(), new Money(command.amount(), java.util.Currency.getInstance(command.currency())),
                            PaymentInstruction.Status.PENDING_RISK, 1, 0, tuple.getT2(), now, now, 0);
                    return risk.assess(payment).flatMap(decision -> {
                        if (!decision.accepted()) return Mono.error(new DomainConflict("risk rejected: " + decision.reason()));
                        var assessed = payment.riskAccepted(decision.requiredApprovals(), clock.instant());
                        return payments.insert(assessed)
                                .flatMap(saved -> accounts.save(tuple.getT3().reserve(saved.amount(), clock.instant())).thenReturn(saved))
                                .flatMap(saved -> audit.record("PAYMENT_CREATED", "PAYMENT", saved.id(), tuple.getT2(),
                                        Map.of("clientReference", saved.clientReference(), "amount", saved.amount().amount().toPlainString())).thenReturn(saved));
                    });
                });
    }

    @Transactional
    public Mono<PaymentInstruction> decide(PaymentCommands.Decide command) {
        return Mono.zip(tenants.currentTenant(), actors.currentActor(), payments.findById(command.paymentId())
                        .switchIfEmpty(Mono.error(new PaymentNotFound(command.paymentId()))))
                .flatMap(tuple -> approvals.exists(command.paymentId(), tuple.getT2()).flatMap(exists -> {
                    if (exists) return Mono.error(new DomainConflict("actor already decided this payment"));
                    var domainDecision = command.decision() == PaymentCommands.Decision.APPROVE
                            ? ApprovalRecord.Decision.APPROVE : ApprovalRecord.Decision.REJECT;
                    var record = new ApprovalRecord(UUID.randomUUID(), tuple.getT1(), command.paymentId(), tuple.getT2(),
                            domainDecision, command.comment(), clock.instant());
                    var updated = command.decision() == PaymentCommands.Decision.APPROVE
                            ? tuple.getT3().approve(clock.instant()) : tuple.getT3().reject(clock.instant());
                    return approvals.insert(record).then(payments.update(updated))
                            .flatMap(saved -> audit.record("PAYMENT_" + domainDecision, "PAYMENT", saved.id(), tuple.getT2(),
                                    Map.of("status", saved.status().name())).thenReturn(saved));
                }));
    }

    @Transactional(readOnly = true)
    public Mono<PaymentInstruction> get(UUID id) { return tenants.currentTenant().then(payments.findById(id)); }
    @Transactional(readOnly = true)
    public Flux<PaymentInstruction> list(int limit, int offset) { return tenants.currentTenant().thenMany(payments.findAll(limit, offset)); }
}

