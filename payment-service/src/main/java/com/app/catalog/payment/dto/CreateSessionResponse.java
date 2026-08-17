package com.app.catalog.payment.dto;

public record CreateSessionResponse(
    String sessionId,
    String checkoutUrl
) {
}
