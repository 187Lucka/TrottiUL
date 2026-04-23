package ca.ulaval.trotti_ul.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import ca.ulaval.trotti_ul.domain.account.PasswordHash;
import ca.ulaval.trotti_ul.domain.security.PasswordHasher;
import ca.ulaval.trotti_ul.domain.common.TechnicalException;

public class SimplePasswordHasher implements PasswordHasher {

    @Override
    public PasswordHash hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            String encoded = Base64.getEncoder().encodeToString(hashed);
            return new PasswordHash(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new TechnicalException("Failed to hash password", e);
        }
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash hash) {
        return hash(rawPassword).value().equals(hash.value());
    }
}
