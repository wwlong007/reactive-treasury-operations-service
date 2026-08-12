package com.acme.treasury.application;

import java.math.BigDecimal;
import java.util.UUID;

public final class PaymentCommands {
    private PaymentCommands() {}
    public record Create(String clientReference, String debitAccountReference, String beneficiaryName,
                         String beneficiaryAccount, BigDecimal amount, String currency) {}
    public record Decide(UUID paymentId, Decision decision, String comment) {}
    public enum Decision { APPROVE, REJECT }
}

