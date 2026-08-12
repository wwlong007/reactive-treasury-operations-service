package com.acme.treasury.domain;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class MoneyTest {
 @Test void keepsCurrencyScaleAndArithmetic(){assertThat(Money.of("12.50","EUR").subtract(Money.of("2.25","EUR")).amount()).isEqualByComparingTo("10.25");}
 @Test void rejectsCrossCurrencyArithmetic(){assertThatThrownBy(()->Money.of("1.00","EUR").add(Money.of("1.00","USD"))).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("currency");}
 @Test void rejectsExcessPrecision(){assertThatThrownBy(()->Money.of("1.001","EUR")).isInstanceOf(ArithmeticException.class);}
}

