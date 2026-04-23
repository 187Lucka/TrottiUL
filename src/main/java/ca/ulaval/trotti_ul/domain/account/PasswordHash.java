package ca.ulaval.trotti_ul.domain.account;

import java.util.Objects;
import java.util.regex.Pattern;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public record PasswordHash(String value) {
    private static final Pattern UPPER = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL = Pattern.compile(".*[^A-Za-z0-9].*");

    public PasswordHash {
        Objects.requireNonNull(value, "hash must not be null");
    }

    public static void validateRawPassword(String raw) {
        if (raw == null || raw.length() < 10) {
            throw new ValidationException("Password must have at least 10 characters");
        }
        if (!UPPER.matcher(raw).matches()) {
            throw new ValidationException("Password must contain an uppercase letter");
        }
        if (!DIGIT.matcher(raw).matches()) {
            throw new ValidationException("Password must contain a digit");
        }
        if (!SPECIAL.matcher(raw).matches()) {
            throw new ValidationException("Password must contain a special character");
        }
    }
}
