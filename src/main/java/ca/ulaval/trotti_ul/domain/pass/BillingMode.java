package ca.ulaval.trotti_ul.domain.pass;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public enum BillingMode {
    PER_TRIP,
    MONTHLY;

    public static BillingMode fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Billing mode must not be empty");
        }
        try {
            return BillingMode.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid billing mode: " + value + ". Must be PER_TRIP or MONTHLY");
        }
    }
}
