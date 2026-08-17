package com.app.catalog.dto;

public record PurchaseResponse(
    String orderTrackingNumber,
    String paymentUrl
) {
}
