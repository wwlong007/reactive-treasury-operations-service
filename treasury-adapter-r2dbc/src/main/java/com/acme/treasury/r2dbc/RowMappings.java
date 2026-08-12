package com.acme.treasury.r2dbc;

import com.acme.treasury.domain.*;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.Currency;

final class RowMappings {
    private RowMappings() {}
    static CashAccount cashAccount(Row row) {
        var currency = Currency.getInstance(row.get("currency", String.class));
        return new CashAccount(row.get("id", java.util.UUID.class), new TenantId(row.get("tenant_id", java.util.UUID.class)),
                row.get("account_reference", String.class), row.get("display_name", String.class),
                new Money(row.get("available_balance", java.math.BigDecimal.class), currency),
                new Money(row.get("reserved_balance", java.math.BigDecimal.class), currency),
                CashAccount.Status.valueOf(row.get("status", String.class)), row.get("version", Long.class),
                row.get("created_at", Instant.class), row.get("updated_at", Instant.class));
    }
    static PaymentInstruction payment(Row row) {
        return new PaymentInstruction(row.get("id", java.util.UUID.class), new TenantId(row.get("tenant_id", java.util.UUID.class)),
                row.get("client_reference", String.class), row.get("debit_account_id", java.util.UUID.class),
                row.get("beneficiary_name", String.class), row.get("beneficiary_account", String.class),
                new Money(row.get("amount", java.math.BigDecimal.class), Currency.getInstance(row.get("currency", String.class))),
                PaymentInstruction.Status.valueOf(row.get("status", String.class)), row.get("required_approvals", Integer.class),
                row.get("approval_count", Integer.class), row.get("created_by", String.class), row.get("created_at", Instant.class),
                row.get("updated_at", Instant.class), row.get("version", Long.class));
    }
}

