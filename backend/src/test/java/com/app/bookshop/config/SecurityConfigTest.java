package com.app.bookshop.config;

import com.app.bookshop.payment.CreatePaymentSessionResponse;
import com.app.bookshop.payment.PaymentClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /** Prevents resource-server startup from calling a live auth issuer. */
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private PaymentClient paymentClient;

    @Test
    void catalogGetWithoutTokenReturns200() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk());
    }

    @Test
    void checkoutPostWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/checkout/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void checkoutPostWithJwtIsNotUnauthorizedBySecurity() throws Exception {
        when(paymentClient.createSession(any()))
            .thenReturn(new CreatePaymentSessionResponse(
                "sess-sec", "http://localhost:8091/checkout/sess-sec"));

        var result = mockMvc.perform(post("/api/v1/checkout/purchase")
                .with(jwt().jwt(j -> j.subject("user-ada"))
                    .authorities(new SimpleGrantedAuthority("SCOPE_bookshop.write")))
                .header("Idempotency-Key", "security-config-test-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "customer": {
                        "firstName": "Ada",
                        "lastName": "Lovelace",
                        "email": "ada@example.com"
                      },
                      "shippingAddress": {
                        "street": "1 Analytical Engine Way",
                        "city": "Lisbon",
                        "stateId": 224,
                        "countryCode": "PT",
                        "zipCode": "1000"
                      },
                      "billingAddress": {
                        "street": "1 Analytical Engine Way",
                        "city": "Lisbon",
                        "stateId": 224,
                        "countryCode": "PT",
                        "zipCode": "1000"
                      },
                      "orderItems": [
                        {
                          "quantity": 1,
                          "productId": 1
                        }
                      ],
                      "currencyCode": "USD"
                    }
                    """))
            .andReturn();

        assertThat(result.getResponse().getStatus()).isNotEqualTo(401);
    }
}
