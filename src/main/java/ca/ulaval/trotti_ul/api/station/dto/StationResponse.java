package ca.ulaval.trotti_ul.api.station.dto;

import java.util.List;

import ca.ulaval.trotti_ul.domain.station.StationSnapshot;
import ca.ulaval.trotti_ul.domain.scooter.ScooterSnapshot;

public record StationResponse(
        String location,
        String name,
        int capacity,
        List<ScooterSlotResponse> scooters
) {
    public static StationResponse from(StationSnapshot snapshot, List<ScooterSnapshot> scooters) {
        var slots = scooters.stream()
                .map(s -> new ScooterSlotResponse(s.slotNumber(), s.energyPercent(), s.occupied()))
                .toList();
        return new StationResponse(snapshot.location().value(), snapshot.name(), snapshot.capacity(), slots);
    }

    public record ScooterSlotResponse(int slotNumber, int energyPercent, boolean occupied) {}
}
