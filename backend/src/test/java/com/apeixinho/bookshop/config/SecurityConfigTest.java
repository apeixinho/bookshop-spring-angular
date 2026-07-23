package com.apeixinho.bookshop.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
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
    @MockBean
    private JwtDecoder jwtDecoder;

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
        var result = mockMvc.perform(post("/api/v1/checkout/purchase")
                .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_bookshop.write")))
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
                        "city": "London",
                        "state": "ENG",
                        "country": "UK",
                        "zipCode": "SW1A"
                      },
                      "billingAddress": {
                        "street": "1 Analytical Engine Way",
                        "city": "London",
                        "state": "ENG",
                        "country": "UK",
                        "zipCode": "SW1A"
                      },
                      "order": {
                        "totalPrice": 19.99,
                        "totalQuantity": 1
                      },
                      "orderItems": [
                        {
                          "imageUrl": "https://example.com/book.png",
                          "quantity": 1,
                          "unitPrice": 19.99,
                          "productId": 1
                        }
                      ]
                    }
                    """))
            .andReturn();

        assertThat(result.getResponse().getStatus()).isNotEqualTo(401);
    }
}
