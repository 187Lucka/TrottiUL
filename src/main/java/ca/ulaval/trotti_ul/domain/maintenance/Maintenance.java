package ca.ulaval.trotti_ul.domain.maintenance;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.technician.TechnicianId;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceAlreadyCompletedException;

public class Maintenance {

    private final MaintenanceId id;
    private final StationLocation stationLocation;
    private final TechnicianId technicianId;
    private final MaintenanceRequestId requestId;
    private final Instant startedAt;
    private Instant completedAt;

    private Maintenance(MaintenanceId id,
                        StationLocation stationLocation,
                        TechnicianId technicianId,
                        MaintenanceRequestId requestId,
                        Instant startedAt,
                        Instant completedAt) {
        this.id = Objects.requireNonNull(id);
        this.stationLocation = Objects.requireNonNull(stationLocation);
        this.technicianId = Objects.requireNonNull(technicianId);
        this.requestId = requestId;
        this.startedAt = Objects.requireNonNull(startedAt);
        this.completedAt = completedAt;
    }

    public static Maintenance start(StationLocation stationLocation,
                                    TechnicianId technicianId,
                                    MaintenanceRequestId requestId,
                                    Instant now) {
        return new Maintenance(
                MaintenanceId.newId(),
                stationLocation,
                technicianId,
                requestId,
                now,
                null
        );
    }

    public MaintenanceId id() {
        return id;
    }

    public StationLocation stationLocation() {
        return stationLocation;
    }

    public TechnicianId technicianId() {
        return technicianId;
    }

    public Optional<MaintenanceRequestId> requestId() {
        return Optional.ofNullable(requestId);
    }

    public Instant startedAt() {
        return startedAt;
    }

    public Optional<Instant> completedAt() {
        return Optional.ofNullable(completedAt);
    }

    public boolean isActive() {
        return completedAt == null;
    }

    public void complete(Instant now) {
        if (!isActive()) {
            throw new MaintenanceAlreadyCompletedException();
        }
        this.completedAt = now;
    }
}
