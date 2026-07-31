package com.app.bookshop.dto;

public record PaymentWebhookRequest(
    String sessionId,
    String status,
    String orderTrackingNumber
) {
}
