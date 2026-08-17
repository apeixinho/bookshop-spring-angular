package com.app.catalog.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Catalog {@code unit_price} values are stored in USD. Convert with fixed demo rates.
 * TRY rate (41.00) is a documented demo default — not supplied by product.
 * Rounding: convert each unit price with HALF_UP to 2dp, then multiply by quantity.
 */
public final class CurrencyRates {

    public static final String CATALOG_CURRENCY = "USD";

    private static final Map<String, BigDecimal> USD_TO;

    static {
        Map<String, BigDecimal> rates = new LinkedHashMap<>();
        rates.put("USD", BigDecimal.ONE);
        rates.put("EUR", new BigDecimal("0.87"));
        rates.put("CAD", new BigDecimal("1.40"));
        rates.put("BRL", new BigDecimal("5.06"));
        rates.put("INR", new BigDecimal("95.52"));
        rates.put("TRY", new BigDecimal("41.00"));
        USD_TO = Collections.unmodifiableMap(rates);
    }

    private CurrencyRates() {
    }

    public static Map<String, BigDecimal> ratesFromUsd() {
        return USD_TO;
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

    public static BigDecimal rate(String currencyCode) {
        return USD_TO.get(normalize(currencyCode));
    }

    public static BigDecimal convertFromUsd(BigDecimal usdAmount, String currencyCode) {
        if (usdAmount == null) {
            throw new IllegalArgumentException("USD amount is required");
        }
        String code = normalize(currencyCode);
        return usdAmount.multiply(USD_TO.get(code)).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal lineTotalFromUsd(BigDecimal usdUnitPrice, int quantity, String currencyCode) {
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be >= 1");
        }
        return convertFromUsd(usdUnitPrice, currencyCode)
            .multiply(BigDecimal.valueOf(quantity))
            .setScale(2, RoundingMode.HALF_UP);
    }
}
