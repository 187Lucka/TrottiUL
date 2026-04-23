package ca.ulaval.trotti_ul.domain.ride;

import java.time.Instant;
import java.util.Objects;
import java.util.Random;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public class GenerateCode {

    private final String value;
    private final Instant expiresAt;

    public GenerateCode(String value, Instant expiresAt) {
        this.value = Objects.requireNonNull(value);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        if (value.length() < 4 || value.length() > 6 || !value.matches("\\d+")) {
            throw new ValidationException("Unlock code must be 4 to 6 digits");
        }
    }

    public static GenerateCode create(Instant now, int validitySeconds) {
        Random random = new Random();
        int length = 4 + random.nextInt(3);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        Instant expiresAt = now.plusSeconds(validitySeconds);
        return new GenerateCode(sb.toString(), expiresAt);
    }

    public String value() {
        return value;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
