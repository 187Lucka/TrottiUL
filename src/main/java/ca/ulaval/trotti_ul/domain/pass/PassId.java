package ca.ulaval.trotti_ul.domain.pass;

import java.util.Objects;
import java.util.UUID;

public record PassId(UUID value) {

    public PassId {
        Objects.requireNonNull(value, "Pass ID must not be null");
    }

    public static PassId newId() {
        return new PassId(UUID.randomUUID());
    }

    public static PassId of(String raw) {
        return new PassId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
