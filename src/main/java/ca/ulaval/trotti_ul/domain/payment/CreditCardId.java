package ca.ulaval.trotti_ul.domain.payment;

import java.util.Objects;
import java.util.UUID;

public record CreditCardId(UUID value) {

    public CreditCardId {
        Objects.requireNonNull(value, "Credit card ID must not be null");
    }

    public static CreditCardId newId() {
        return new CreditCardId(UUID.randomUUID());
    }

    public static CreditCardId of(String raw) {
        return new CreditCardId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
