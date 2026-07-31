package com.app.bookshop.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;

/**
 * Catalog {@code unit_price} values are stored in USD. Convert with fixed demo rates.
 * TRY rate (41.00) is a documented demo default — not supplied by product.
 */
public final class CurrencyRates {

    public static final String CATALOG_CURRENCY = "USD";

    private static final Map<String, BigDecimal> USD_TO = Map.of(
        "USD", BigDecimal.ONE,
        "EUR", new BigDecimal("0.87"),
        "CAD", new BigDecimal("1.40"),
        "BRL", new BigDecimal("5.06"),
        "INR", new BigDecimal("95.52"),
        "TRY", new BigDecimal("41.00")
    );

    private CurrencyRates() {
    }

    public static String normalize(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return CATALOG_CURRENCY;
        }
        String code = currencyCode.trim().toUpperCase(Locale.ROOT);
        if (!USD_TO.containsKey(code)) {
            throw new IllegalArgumentException("Unsupported currencyCode: " + currencyCode);
        }
        return code;
    }

    public static BigDecimal convertFromUsd(BigDecimal usdAmount, String currencyCode) {
        String code = normalize(currencyCode);
        if (usdAmount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return usdAmount.multiply(USD_TO.get(code)).setScale(2, RoundingMode.HALF_UP);
    }
}
