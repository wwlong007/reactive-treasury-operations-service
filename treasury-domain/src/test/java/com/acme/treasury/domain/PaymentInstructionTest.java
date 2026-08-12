package com.acme.treasury.domain;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class PaymentInstructionTest {
    @Test void reachesApprovedOnlyAfterRequiredVotes() {
        var now = Instant.parse("2026-01-01T00:00:00Z");
        var payment = new PaymentInstruction(UUID.randomUUID(), new TenantId(UUID.randomUUID()), "P-1", UUID.randomUUID(),
                "Supplier", "DE001", Money.of("10.00", "EUR"), PaymentInstruction.Status.PENDING_RISK, 1, 0, "maker", now, now, 0);
        assertThat(payment.riskAccepted(2, now).approve(now).status()).isEqualTo(PaymentInstruction.Status.AWAITING_APPROVAL);
        assertThat(payment.riskAccepted(2, now).approve(now).approve(now).status()).isEqualTo(PaymentInstruction.Status.APPROVED);
    }
}

