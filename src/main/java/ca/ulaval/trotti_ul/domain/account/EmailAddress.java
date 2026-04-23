package ca.ulaval.trotti_ul.domain.account;

import java.util.Objects;
import java.util.regex.Pattern;

public record EmailAddress(String value) {

    private static final Pattern SIMPLE_EMAIL =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailAddress {
        Objects.requireNonNull(value, "email must not be null");
        if (!SIMPLE_EMAIL.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
    }

    public static EmailAddress of(String raw) {
        return new EmailAddress(raw.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }
}