package ca.ulaval.trotti_ul.domain.account;

import java.util.Objects;
import java.util.UUID;

public record AccountId(UUID value) {

    public AccountId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static AccountId newId() {
        return new AccountId(UUID.randomUUID());
    }

    public static AccountId fromString(String raw) {
        return new AccountId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
