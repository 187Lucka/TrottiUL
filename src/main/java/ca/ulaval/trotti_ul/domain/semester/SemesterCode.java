package ca.ulaval.trotti_ul.domain.semester;

import java.util.Objects;
import java.util.regex.Pattern;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public record SemesterCode(String value) {

    private static final Pattern VALID_FORMAT = Pattern.compile("^[AHE]\\d{2}$");

    public SemesterCode {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Semester code must not be empty");
        }
        String normalized = value.toUpperCase().trim();
        if (!VALID_FORMAT.matcher(normalized).matches()) {
            throw new ValidationException("Semester code must match format [A|H|E]XX (e.g., A25, H26, E26)");
        }
        value = normalized;
    }

    public static SemesterCode of(String raw) {
        return new SemesterCode(raw);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemesterCode that = (SemesterCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
