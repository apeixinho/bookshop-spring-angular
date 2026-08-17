package com.app.catalog.currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CurrencyRatesTest {

    @Test
    void convertsUnitThenLineTotalWithHalfUp() {
        BigDecimal usd = new BigDecimal("14.99");
        assertThat(CurrencyRates.convertFromUsd(usd, "EUR")).isEqualByComparingTo("13.04");
        assertThat(CurrencyRates.lineTotalFromUsd(usd, 4, "EUR")).isEqualByComparingTo("52.16");
        assertThat(CurrencyRates.convertFromUsd(usd, "TRY")).isEqualByComparingTo("614.59");
    }

    @Test
    void rejectsNullUsdAmount() {
        assertThatThrownBy(() -> CurrencyRates.convertFromUsd(null, "USD"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
