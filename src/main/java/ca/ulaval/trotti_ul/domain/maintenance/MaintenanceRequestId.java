package ca.ulaval.trotti_ul.domain.maintenance;

import java.util.Objects;
import java.util.UUID;

public final class MaintenanceRequestId {

    private final UUID value;

    private MaintenanceRequestId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static MaintenanceRequestId newId() {
        return new MaintenanceRequestId(UUID.randomUUID());
    }

    public static MaintenanceRequestId fromString(String value) {
        return new MaintenanceRequestId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaintenanceRequestId that = (MaintenanceRequestId) o;
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
