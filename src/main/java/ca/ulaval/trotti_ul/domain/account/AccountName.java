package ca.ulaval.trotti_ul.domain.account;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public record AccountName(String value) {

    public AccountName {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Account name must not be empty");
        }
        String trimmed = value.trim();
        if (trimmed.length() < 2) {
            throw new ValidationException("Account name must have at least 2 characters");
        }
        value = trimmed;
    }

    public static AccountName of(String raw) {
        return new AccountName(raw);
    }

    @Override
    public String toString() {
        return value;
    }
}
