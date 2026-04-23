package ca.ulaval.trotti_ul.domain.maintenance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.technician.TechnicianId;

public class TechnicianTruck {

    private final TechnicianId technicianId;
    private final List<TransferredScooter> scooters;

    public TechnicianTruck(TechnicianId technicianId) {
        this.technicianId = Objects.requireNonNull(technicianId);
        this.scooters = new ArrayList<>();
    }

    public TechnicianId technicianId() {
        return technicianId;
    }

    public List<TransferredScooter> scooters() {
        return Collections.unmodifiableList(scooters);
    }

    public boolean isEmpty() {
        return scooters.isEmpty();
    }

    public int scooterCount() {
        return scooters.size();
    }

    public void loadScooter(TransferredScooter scooter) {
        scooters.add(scooter);
    }

    public void requireEmpty() {
        if (!isEmpty()) {
            throw new TruckNotEmptyException();
        }
    }

    public Optional<TransferredScooter> unloadScooter(String scooterId) {
        for (int i = 0; i < scooters.size(); i++) {
            if (scooters.get(i).scooterId().equals(scooterId)) {
                return Optional.of(scooters.remove(i));
            }
        }
        return Optional.empty();
    }

    public Optional<TransferredScooter> findScooter(String scooterId) {
        return scooters.stream()
                .filter(s -> s.scooterId().equals(scooterId))
                .findFirst();
    }
}
