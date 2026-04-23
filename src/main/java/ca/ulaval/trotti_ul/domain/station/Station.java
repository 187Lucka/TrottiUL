package ca.ulaval.trotti_ul.domain.station;

import java.util.Objects;

public class Station {
    private final StationLocation location;
    private final String name;
    private final int capacity;

    public Station(StationLocation location, String name, int capacity) {
        this.location = Objects.requireNonNull(location);
        this.name = Objects.requireNonNull(name);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Station capacity must be positive");
        }
        this.capacity = capacity;
    }

    public StationSnapshot snapshot() {
        return new StationSnapshot(location, name, capacity);
    }

    public StationLocation location() {
        return location;
    }

    public int capacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "Station{" +
                "location=" + location +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
