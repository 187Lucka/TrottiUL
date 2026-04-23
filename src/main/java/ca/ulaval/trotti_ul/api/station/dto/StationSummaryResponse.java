package ca.ulaval.trotti_ul.api.station.dto;

import ca.ulaval.trotti_ul.domain.station.StationSnapshot;

public record StationSummaryResponse(
        String location,
        String name,
        int scooters,
        int capacity
) {
    public static StationSummaryResponse from(StationSnapshot snapshot, int scootersCount) {
        return new StationSummaryResponse(
                snapshot.location().value(),
                snapshot.name(),
                scootersCount,
                snapshot.capacity()
        );
    }
}
