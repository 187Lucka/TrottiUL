package ca.ulaval.trotti_ul.domain.scooter;

import ca.ulaval.trotti_ul.domain.station.ScooterUnavailableException;
import ca.ulaval.trotti_ul.domain.station.StationLocation;

public interface ScooterReservation {
    int reserveScooter(StationLocation station, int slotNumber) throws ScooterUnavailableException;
    void returnScooter(StationLocation station, int slotNumber, int energyPercent);
    int removeForTransfer(StationLocation station, int slotNumber);
}
