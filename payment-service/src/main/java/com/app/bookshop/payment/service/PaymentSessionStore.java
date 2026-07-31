package com.app.bookshop.payment.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.app.bookshop.payment.dto.CreateSessionRequest;
import com.app.bookshop.payment.dto.CreateSessionResponse;
import com.app.bookshop.payment.model.PaymentSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentSessionStore {

    private final Map<String, PaymentSession> sessions = new ConcurrentHashMap<>();
    private final String publicBaseUrl;

    public PaymentSessionStore(
        @Value("${bookshop.payment.public-base-url}") String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl.replaceAll("/$", "");
    }

    public CreateSessionResponse create(CreateSessionRequest request) {
        String id = UUID.randomUUID().toString();
        PaymentSession session = new PaymentSession(
            id,
            request.amount(),
            request.currency().trim().toUpperCase(),
            request.orderTrackingNumber().trim(),
            request.successUrl().trim(),
            request.cancelUrl().trim());
        sessions.put(id, session);
        return new CreateSessionResponse(id, publicBaseUrl + "/checkout/" + id);
    }

    public Optional<PaymentSession> find(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }
}
