package ca.ulaval.trotti_ul.domain.maintenance;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.station.StationLocation;

public class MaintenanceRequest {

    private final MaintenanceRequestId id;
    private final StationLocation stationLocation;
    private final AccountId requestedBy;
    private final String reason;
    private MaintenanceRequestStatus status;
    private final Instant createdAt;

    private MaintenanceRequest(MaintenanceRequestId id,
                               StationLocation stationLocation,
                               AccountId requestedBy,
                               String reason,
                               MaintenanceRequestStatus status,
                               Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.stationLocation = Objects.requireNonNull(stationLocation);
        this.requestedBy = requestedBy;
        this.reason = Objects.requireNonNull(reason);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static MaintenanceRequest create(StationLocation stationLocation,
                                            AccountId requestedBy,
                                            String reason,
                                            Instant now) {
        return new MaintenanceRequest(
                MaintenanceRequestId.newId(),
                stationLocation,
                requestedBy,
                reason,
                MaintenanceRequestStatus.PENDING,
                now
        );
    }

    public MaintenanceRequestId id() {
        return id;
    }

    public StationLocation stationLocation() {
        return stationLocation;
    }

    public Optional<AccountId> requestedBy() {
        return Optional.ofNullable(requestedBy);
    }

    public String reason() {
        return reason;
    }

    public MaintenanceRequestStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public void markInProgress() {
        this.status = MaintenanceRequestStatus.IN_PROGRESS;
    }

    public void markCompleted() {
        this.status = MaintenanceRequestStatus.COMPLETED;
    }
}
