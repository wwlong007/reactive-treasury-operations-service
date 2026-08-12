package com.acme.treasury.application.port;
import com.acme.treasury.domain.PaymentInstruction;
import reactor.core.publisher.Mono;
public interface RiskAssessmentPort {
    Mono<RiskDecision> assess(PaymentInstruction instruction);
    record RiskDecision(boolean accepted, int requiredApprovals, String reason) {
        public static RiskDecision accept(int approvals) { return new RiskDecision(true, approvals, "accepted"); }
        public static RiskDecision reject(String reason) { return new RiskDecision(false, 0, reason); }
    }
}

