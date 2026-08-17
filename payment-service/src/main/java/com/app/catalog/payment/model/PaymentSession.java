package com.app.catalog.payment.model;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentSession {

    public enum Status {
        OPEN,
        SUCCEEDED,
        CANCELLED,
        FAILED
    }

    private final String id;
    private final BigDecimal amount;
    private final String currency;
    private final String orderTrackingNumber;
    private final String successUrl;
    private final String cancelUrl;
    private final Instant createdAt;
    private Status status;
    private String failureReason;

    public PaymentSession(
        String id,
        BigDecimal amount,
        String currency,
        String orderTrackingNumber,
        String successUrl,
        String cancelUrl) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.orderTrackingNumber = orderTrackingNumber;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.createdAt = Instant.now();
        this.status = Status.OPEN;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getOrderTrackingNumber() {
        return orderTrackingNumber;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
