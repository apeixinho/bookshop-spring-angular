package com.app.catalog.auth.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.EnumSet;
import java.util.Set;

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
        @Value("${catalog.auth.jwk-path:./data/auth-jwk.json}") String jwkPath) throws IOException {
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
            restrictOwnerReadWrite(path);
        }
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    private static void restrictOwnerReadWrite(Path path) {
        try {
            Set<PosixFilePermission> perms = EnumSet.of(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(path, perms);
        } catch (UnsupportedOperationException ignored) {
            // Windows / non-POSIX FS — best-effort only
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to set permissions on JWK file " + path, ex);
        }
    }

    private static RSAKey generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID("eshop-auth-server")
                .build();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate RSA key for JWT signing", ex);
        }
    }
}
