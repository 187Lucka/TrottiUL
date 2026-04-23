package ca.ulaval.trotti_ul.domain.scooter;

import java.time.Instant;
import java.util.List;

import ca.ulaval.trotti_ul.domain.station.StationLocation;

public interface ScooterEnergyRepository {
    /**
     * Low-level accessors to manipulate energy values externally (e.g., domain services).
     * Implementations should return defensive copies.
     */
    List<Integer> getEnergies(StationLocation station);
    List<Instant> getLastUpdated(StationLocation station);
    void saveEnergies(StationLocation station, List<Integer> energies, List<Instant> lastUpdated);
}
