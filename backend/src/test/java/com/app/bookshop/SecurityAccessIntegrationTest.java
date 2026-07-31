package com.app.bookshop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.app.bookshop.entity.Product;
import com.app.bookshop.payment.CreatePaymentSessionResponse;
import com.app.bookshop.payment.PaymentClient;
import com.app.bookshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SecurityAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private PaymentClient paymentClient;

    @BeforeEach
    void restoreStock() {
        Product product = productRepository.findById(1L).orElseThrow();
        product.setUnitsInStock(100);
        productRepository.saveAndFlush(product);
    }

    @Test
    void catalogIsPublic() throws Exception {
        mockMvc.perform(get("/api/v1/products").param("page", "0").param("size", "5"))
            .andExpect(status().isOk());
    }

    @Test
    void checkoutRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/checkout/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void checkoutInvalidBodyReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/checkout/purchase")
                .with(jwt().jwt(j -> j.subject("user-1").claim("scope", "bookshop.write"))
                    .authorities(() -> "SCOPE_bookshop.write"))
                .header("Idempotency-Key", "test-key-invalid-body")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void checkoutAcceptedWithWriteScope() throws Exception {
        when(paymentClient.createSession(any()))
            .thenReturn(new CreatePaymentSessionResponse(
                "sess-1", "http://localhost:8091/checkout/sess-1"));

        String body = """
            {
              "customer":{"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com"},
              "orderItems":[{"quantity":1,"productId":1}],
              "shippingAddress":{"street":"1 St","city":"Lisbon","stateId":224,"countryCode":"PT","zipCode":"1000"},
              "billingAddress":{"street":"1 St","city":"Lisbon","stateId":224,"countryCode":"PT","zipCode":"1000"},
              "currencyCode":"USD"
            }
            """;

        mockMvc.perform(post("/api/v1/checkout/purchase")
                .with(jwt().jwt(j -> j.subject("user-ada").claim("scope", "bookshop.write"))
                    .authorities(() -> "SCOPE_bookshop.write"))
                .header("Idempotency-Key", "test-key-checkout-ok")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderTrackingNumber").isNotEmpty())
            .andExpect(jsonPath("$.paymentUrl").value("http://localhost:8091/checkout/sess-1"));
    }

    @Test
    void paymentWebhookRequiresSecret() throws Exception {
        mockMvc.perform(post("/api/v1/checkout/payment-webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"sessionId":"x","status":"SUCCEEDED","orderTrackingNumber":"y"}
                    """))
            .andExpect(status().isUnauthorized());
    }
}
