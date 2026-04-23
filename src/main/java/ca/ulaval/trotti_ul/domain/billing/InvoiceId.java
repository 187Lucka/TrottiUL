package ca.ulaval.trotti_ul.domain.billing;

import java.util.Objects;
import java.util.UUID;

public record InvoiceId(UUID value) {

    public InvoiceId {
        Objects.requireNonNull(value, "Invoice ID must not be null");
    }

    public static InvoiceId newId() {
        return new InvoiceId(UUID.randomUUID());
    }

    public static InvoiceId of(String raw) {
        return new InvoiceId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
