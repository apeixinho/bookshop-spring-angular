package com.app.bookshop.payment.dto;

public record WebhookPayload(
    String sessionId,
    String status,
    String orderTrackingNumber
) {
}
