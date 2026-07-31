package com.app.bookshop.payment.service;

import com.app.bookshop.payment.dto.WebhookPayload;
import com.app.bookshop.payment.model.PaymentSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class BookshopWebhookClient {

    private static final Logger log = LoggerFactory.getLogger(BookshopWebhookClient.class);

    private final RestClient restClient;
    private final String webhookUrl;
    private final String webhookSecret;

    public BookshopWebhookClient(
        RestClient.Builder restClientBuilder,
        @Value("${bookshop.payment.webhook-url}") String webhookUrl,
        @Value("${bookshop.payment.webhook-secret}") String webhookSecret) {
        this.restClient = restClientBuilder.build();
        this.webhookUrl = webhookUrl;
        this.webhookSecret = webhookSecret;
    }

    /**
     * @return null on success; failure reason when bookshop rejects (e.g. stock)
     */
    public String notify(PaymentSession session, PaymentSession.Status status) {
        WebhookPayload payload = new WebhookPayload(
            session.getId(),
            status.name(),
            session.getOrderTrackingNumber());
        try {
            restClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Payment-Secret", webhookSecret)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
            return null;
        } catch (RestClientResponseException ex) {
            log.warn(
                "Webhook rejected session={} status={} httpStatus={} body={}",
                session.getId(),
                status,
                ex.getStatusCode().value(),
                ex.getResponseBodyAsString());
            return ex.getResponseBodyAsString().isBlank()
                ? "Payment could not be finalized (" + ex.getStatusCode().value() + ")"
                : ex.getResponseBodyAsString();
        } catch (Exception ex) {
            log.error("Webhook failed session={}", session.getId(), ex);
            return "Payment service could not reach bookshop";
        }
    }
}
