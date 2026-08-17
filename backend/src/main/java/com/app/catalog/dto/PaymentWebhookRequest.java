package com.app.catalog.dto;

public record PaymentWebhookRequest(
    String sessionId,
    String status,
    String orderTrackingNumber
) {
}
