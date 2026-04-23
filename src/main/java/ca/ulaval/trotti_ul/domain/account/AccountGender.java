package ca.ulaval.trotti_ul.domain.account;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public record AccountGender(Gender value) {

    public enum Gender {
        MALE,
        FEMALE,
        NON_BINARY,
        UNDISCLOSED
    }

    public static AccountGender from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new ValidationException("Account gender is required");
        }
        try {
            return new AccountGender(Gender.valueOf(raw.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid account gender: " + raw);
        }
    }

    public String asString() {
        return value.name();
    }
}
