package ca.ulaval.trotti_ul.domain.station;

public record StationSnapshot(
        StationLocation location,
        String name,
        int capacity
) {}
