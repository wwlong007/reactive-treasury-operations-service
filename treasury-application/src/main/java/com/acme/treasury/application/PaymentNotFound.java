package com.acme.treasury.application;
import java.util.UUID;
public final class PaymentNotFound extends RuntimeException { public PaymentNotFound(UUID id) { super("payment not found: " + id); } }

