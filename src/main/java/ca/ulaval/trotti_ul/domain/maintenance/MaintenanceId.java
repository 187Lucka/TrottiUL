package ca.ulaval.trotti_ul.domain.maintenance;

import java.util.Objects;
import java.util.UUID;

public final class MaintenanceId {

    private final UUID value;

    private MaintenanceId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static MaintenanceId newId() {
        return new MaintenanceId(UUID.randomUUID());
    }

    public static MaintenanceId fromString(String value) {
        return new MaintenanceId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaintenanceId that = (MaintenanceId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
