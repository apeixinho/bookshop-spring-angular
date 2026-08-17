package com.app.catalog.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

    private final RestClient restClient;
    private final String apiSecret;

    public PaymentClient(
        RestClient.Builder restClientBuilder,
        @Value("${catalog.payment.base-url:http://localhost:8091}") String baseUrl,
        @Value("${catalog.payment.api-secret:dev-payment-secret}") String apiSecret) {
        this.restClient = restClientBuilder.baseUrl(baseUrl.replaceAll("/$", "")).build();
        this.apiSecret = apiSecret;
    }

    public CreatePaymentSessionResponse createSession(CreatePaymentSessionRequest request) {
        return restClient.post()
            .uri("/api/v1/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Payment-Secret", apiSecret)
            .body(request)
            .retrieve()
            .body(CreatePaymentSessionResponse.class);
    }
}
