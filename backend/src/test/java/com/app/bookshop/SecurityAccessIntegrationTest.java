package com.app.bookshop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SecurityAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
            .andExpect(status().isOk());
    }
}
