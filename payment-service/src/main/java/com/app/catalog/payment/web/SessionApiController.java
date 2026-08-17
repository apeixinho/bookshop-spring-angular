package com.app.catalog.payment.web;

import com.app.catalog.payment.dto.CreateSessionRequest;
import com.app.catalog.payment.dto.CreateSessionResponse;
import com.app.catalog.payment.service.PaymentSessionStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionApiController {

    private final PaymentSessionStore store;
    private final String apiSecret;

    public SessionApiController(
        PaymentSessionStore store,
        @Value("${catalog.payment.api-secret}") String apiSecret) {
        this.store = store;
        this.apiSecret = apiSecret;
    }

    @PostMapping
    public CreateSessionResponse create(
        @RequestHeader(value = "X-Payment-Secret", required = false) String secret,
        @Valid @RequestBody CreateSessionRequest request) {

        if (secret == null || !secret.equals(apiSecret)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid payment");
        }
        return store.create(request);
    }
}
