package com.app.bookshop.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthServerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Test
    void contextLoadsWithSpaClient() {
        assertThat(registeredClientRepository.findByClientId("bookshop-spa")).isNotNull();
        assertThat(registeredClientRepository.findByClientId("bookshop-spa").getScopes())
            .contains("openid", "profile", "bookshop.read", "bookshop.write");
    }

    @Test
    void actuatorHealthIsPermitAll() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void authorizationServerMetadataExposesIssuer() throws Exception {
        mockMvc.perform(get("/.well-known/oauth-authorization-server")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.issuer").value("http://localhost:9000"));
    }

    @Test
    void loginPageIsReachable() throws Exception {
        mockMvc.perform(get("/login").accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk());
    }
}
