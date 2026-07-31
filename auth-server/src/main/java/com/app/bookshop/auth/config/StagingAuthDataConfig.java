package com.app.bookshop.auth.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration
@Profile("staging")
public class StagingAuthDataConfig {

    @Value("${bookshop.auth.frontend-origin:http://localhost:4200}")
    private String frontendOrigin;

    @Value("${bookshop.auth.client-id:bookshop-spa}")
    private String clientId;

    @Bean
    UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery(
            "select username, password, enabled from users where username = ?");
        users.setAuthoritiesByUsernameQuery(
            "select username, authority from authorities where username = ?");
        return users;
    }

    @Bean
    RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    OAuth2AuthorizationService authorizationService(
        JdbcTemplate jdbcTemplate,
        RegisteredClientRepository registeredClientRepository
    ) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    OAuth2AuthorizationConsentService authorizationConsentService(
        JdbcTemplate jdbcTemplate,
        RegisteredClientRepository registeredClientRepository
    ) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    ApplicationRunner seedSpaClient(RegisteredClientRepository clients) {
        return args -> {
            RegisteredClient desired = DevAuthDataConfig.spaClient(clientId, frontendOrigin);
            RegisteredClient existing = clients.findByClientId(clientId);
            if (existing == null) {
                clients.save(desired);
                return;
            }
            // Reconcile redirect URIs / settings when FRONTEND_ORIGIN or scopes change.
            clients.save(RegisteredClient.from(existing)
                .redirectUris(uris -> {
                    uris.clear();
                    uris.addAll(desired.getRedirectUris());
                })
                .postLogoutRedirectUris(uris -> {
                    uris.clear();
                    uris.addAll(desired.getPostLogoutRedirectUris());
                })
                .scopes(scopes -> {
                    scopes.clear();
                    scopes.addAll(desired.getScopes());
                })
                .clientSettings(desired.getClientSettings())
                .tokenSettings(desired.getTokenSettings())
                .build());
        };
    }
}
