package com.app.catalog.payment;

import java.math.BigDecimal;

public record CreatePaymentSessionRequest(
    BigDecimal amount,
    String currency,
    String orderTrackingNumber,
    String successUrl,
    String cancelUrl
) {
}
