package ca.ulaval.trotti_ul.domain.scooter;

import ca.ulaval.trotti_ul.domain.station.StationLocation;

public record ScooterSnapshot(
        String id,
        StationLocation station,
        int slotNumber,
        int energyPercent,
        boolean occupied
) {}
