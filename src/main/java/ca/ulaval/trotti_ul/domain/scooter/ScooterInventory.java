package ca.ulaval.trotti_ul.domain.scooter;

import java.util.List;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.station.StationLocation;

public interface ScooterInventory {
    List<ScooterSnapshot> findByStation(StationLocation station);
    int countByStation(StationLocation station);
    Optional<ScooterSnapshot> findByStationAndSlot(StationLocation station, int slotNumber);
    boolean isSlotEmpty(StationLocation station, int slotNumber);
}
