package ca.ulaval.trotti_ul.domain.scooter;

import java.util.Objects;
import java.util.UUID;

import ca.ulaval.trotti_ul.domain.station.ScooterUnavailableException;
import ca.ulaval.trotti_ul.domain.station.StationLocation;

public class Scooter {

    private final String id;
    private StationLocation station;
    private int slotNumber;
    private int energyPercent;
    private boolean reserved;

    public Scooter(String id, StationLocation station, int slotNumber, int energyPercent, boolean reserved) {
        this.id = Objects.requireNonNull(id);
        this.station = Objects.requireNonNull(station);
        this.slotNumber = slotNumber;
        this.energyPercent = energyPercent;
        this.reserved = reserved;
    }

    public static Scooter create(StationLocation station, int slotNumber, int energyPercent) {
        return new Scooter(UUID.randomUUID().toString(), station, slotNumber, energyPercent, false);
    }

    public void ensureAvailable() {
        if (reserved) {
            throw new ScooterUnavailableException("Scooter already reserved");
        }
        if (energyPercent <= 15) {
            throw new ScooterUnavailableException("Scooter energy too low");
        }
    }

    public void reserve() {
        ensureAvailable();
        reserved = true;
    }

    public void returnTo(StationLocation station, int slotNumber, int energyPercent) {
        this.station = station;
        this.slotNumber = slotNumber;
        this.energyPercent = energyPercent;
        this.reserved = false;
    }

    public ScooterSnapshot snapshot() {
        return new ScooterSnapshot(id, station, slotNumber, energyPercent, reserved);
    }

    public String id() {
        return id;
    }

    public StationLocation station() {
        return station;
    }

    public int slotNumber() {
        return slotNumber;
    }

    public boolean reserved() {
        return reserved;
    }
}
