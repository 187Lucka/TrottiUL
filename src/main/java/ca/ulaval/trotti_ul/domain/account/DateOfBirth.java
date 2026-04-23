package ca.ulaval.trotti_ul.domain.account;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public record DateOfBirth(LocalDate value) {

    public DateOfBirth {
        Objects.requireNonNull(value, "date of birth must not be null");
        if (value.isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth cannot be in the future");
        }
    }

    public static DateOfBirth from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new ValidationException("Date of birth is required");
        }
        try {
            LocalDate date = LocalDate.parse(raw.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            return new DateOfBirth(date);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date of birth format, expected ISO yyyy-MM-dd");
        }
    }

    public static DateOfBirth undisclosed() {
        return new DateOfBirth(LocalDate.of(1900, 1, 1));
    }

    public int ageOn(LocalDate today) {
        if (today.isBefore(value)) {
            throw new ValidationException("Reference date cannot be before date of birth");
        }
        return Period.between(value, today).getYears();
    }
}
