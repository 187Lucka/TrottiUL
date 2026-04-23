package ca.ulaval.trotti_ul.domain.maintenance;

import java.util.Objects;

import ca.ulaval.trotti_ul.domain.station.StationLocation;

public record TransferredScooter(
        String scooterId,
        StationLocation originStation,
        int originSlotNumber,
        int energyPercent
) {
    public TransferredScooter {
        Objects.requireNonNull(scooterId);
        Objects.requireNonNull(originStation);
        if (originSlotNumber <= 0) {
            throw new IllegalArgumentException("Slot number must be positive");
        }
        if (energyPercent < 0 || energyPercent > 100) {
            throw new IllegalArgumentException("Energy percent must be between 0 and 100");
        }
    }
}
