package com.app.bookshop.payment;

public record CreatePaymentSessionResponse(
    String sessionId,
    String checkoutUrl
) {
}
