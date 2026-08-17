package com.app.catalog.payment;

public record CreatePaymentSessionResponse(
    String sessionId,
    String checkoutUrl
) {
}
