package com.app.catalog.controller;

import com.app.catalog.dto.PaymentWebhookRequest;
import com.app.catalog.services.CheckoutService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PaymentWebhookController {

    private final CheckoutService checkoutService;
    private final String webhookSecret;

    public PaymentWebhookController(
        CheckoutService checkoutService,
        @Value("${catalog.payment.webhook-secret:dev-payment-secret}") String webhookSecret) {
        this.checkoutService = checkoutService;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/api/v1/checkout/payment-webhook")
    public void paymentWebhook(
        @RequestHeader(value = "X-Payment-Secret", required = false) String secret,
        @RequestBody PaymentWebhookRequest request) {

        if (secret == null || !secret.equals(webhookSecret)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid payment secret");
        }
        checkoutService.finalizePayment(request);
    }
}
