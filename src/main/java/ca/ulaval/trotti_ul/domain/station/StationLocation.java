package ca.ulaval.trotti_ul.domain.station;

import java.util.Objects;

public final class StationLocation {
    private final String value;

    public StationLocation(String value) {
        Objects.requireNonNull(value, "Station location is required");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Station location is required");
        }
        this.value = trimmed.toUpperCase();
    }

    public static StationLocation fromString(String raw) {
        return new StationLocation(raw);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationLocation that = (StationLocation) o;
        return value.equals(that.value);
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
