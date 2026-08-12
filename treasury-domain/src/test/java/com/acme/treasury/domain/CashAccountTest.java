package com.acme.treasury.domain;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class CashAccountTest {
 private CashAccount account(String available){var now=Instant.parse("2026-01-01T00:00:00Z");return new CashAccount(UUID.randomUUID(),new TenantId(UUID.randomUUID()),"OPERATING","Operating account",Money.of(available,"EUR"),Money.of("0.00","EUR"),CashAccount.Status.ACTIVE,0,now,now);}
 @Test void reservationMovesFundsAtomically(){var result=account("250.00").reserve(Money.of("75.00","EUR"),Instant.parse("2026-01-02T00:00:00Z"));assertThat(result.availableBalance().amount()).isEqualByComparingTo("175.00");assertThat(result.reservedBalance().amount()).isEqualByComparingTo("75.00");assertThat(result.version()).isOne();}
 @Test void rejectsOverdraft(){assertThatThrownBy(()->account("10.00").reserve(Money.of("10.01","EUR"),Instant.now())).isInstanceOf(DomainConflict.class);}
 @Test void rejectsReservationOnSuspendedAccount(){var a=account("10.00");var suspended=new CashAccount(a.id(),a.tenantId(),a.accountReference(),a.displayName(),a.availableBalance(),a.reservedBalance(),CashAccount.Status.SUSPENDED,a.version(),a.createdAt(),a.updatedAt());assertThatThrownBy(()->suspended.reserve(Money.of("1.00","EUR"),Instant.now())).isInstanceOf(DomainConflict.class);}
}

