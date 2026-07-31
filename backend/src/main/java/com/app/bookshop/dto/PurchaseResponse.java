package com.app.bookshop.dto;

public record PurchaseResponse(
    String orderTrackingNumber,
    String paymentUrl
) {
}
