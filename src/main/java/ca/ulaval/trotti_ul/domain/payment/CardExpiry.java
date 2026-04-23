package ca.ulaval.trotti_ul.domain.payment;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public record CardExpiry(YearMonth value) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    public CardExpiry {
        if (value == null) {
            throw new ValidationException("Card expiry must not be null");
        }
    }

    public static CardExpiry of(String expiryString) {
        if (expiryString == null || expiryString.isBlank()) {
            throw new ValidationException("Card expiry must not be empty");
        }

        try {
            YearMonth yearMonth = YearMonth.parse(expiryString, FORMATTER);
            return new CardExpiry(yearMonth);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Card expiry must be in format MM/yy");
        }
    }

    public boolean isExpired(YearMonth currentMonth) {
        return value.isBefore(currentMonth);
    }

    @Override
    public String toString() {
        return value.format(FORMATTER);
    }
}
