package ca.ulaval.trotti_ul.domain.technician;

import java.util.Objects;
import java.util.UUID;

public record TechnicianId(UUID value) {

    public TechnicianId {
        Objects.requireNonNull(value);
    }

    public static TechnicianId newId() {
        return new TechnicianId(UUID.randomUUID());
    }

    public static TechnicianId fromString(String raw) {
        return new TechnicianId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}