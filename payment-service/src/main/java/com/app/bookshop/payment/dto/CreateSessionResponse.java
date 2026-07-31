package com.app.bookshop.payment.dto;

public record CreateSessionResponse(
    String sessionId,
    String checkoutUrl
) {
}
