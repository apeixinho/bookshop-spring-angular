package com.app.bookshop.auth.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class JwkConfig {

    @Bean
    JWKSource<SecurityContext> jwkSource(
        @Value("${bookshop.auth.jwk-path:./data/auth-jwk.json}") String jwkPath) throws IOException {
        Path path = Path.of(jwkPath).toAbsolutePath().normalize();
        RSAKey rsaKey;
        if (Files.exists(path)) {
            try {
                rsaKey = RSAKey.parse(Files.readString(path));
            } catch (ParseException ex) {
                throw new IllegalStateException("Invalid JWK file at " + path, ex);
            }
        } else {
            rsaKey = generateRsaKey();
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, rsaKey.toJSONString());
        }
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    private static RSAKey generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID("bookshop-auth-server")
                .build();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate RSA key for JWT signing", ex);
        }
    }
}
