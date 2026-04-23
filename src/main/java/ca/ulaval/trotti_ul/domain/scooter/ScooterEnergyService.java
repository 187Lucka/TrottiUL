package ca.ulaval.trotti_ul.domain.scooter;

import java.time.Duration;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRepository;
import ca.ulaval.trotti_ul.domain.station.StationLocation;

public class ScooterEnergyService {

    private final ScooterEnergyRepository scooterRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final Clock clock;

    public ScooterEnergyService(ScooterEnergyRepository scooterRepository, MaintenanceRepository maintenanceRepository, Clock clock) {
        this.scooterRepository = scooterRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.clock = clock;
    }

    public void applyRechargeIfAllowed(StationLocation station) {
        if (maintenanceRepository.findActiveByStation(station).isPresent()) {
            return;
        }

        List<Integer> energies = scooterRepository.getEnergies(station);
        List<Instant> updates = scooterRepository.getLastUpdated(station);
        if (energies == null || updates == null || energies.size() != updates.size()) {
            return;
        }

        Instant now = Instant.now(clock);
        for (int i = 0; i < energies.size(); i++) {
            int energy = energies.get(i);
            Instant last = updates.get(i);
            long minutes = Duration.between(last, now).toMinutes();
            if (minutes > 0 && energy < 100) {
                int gained = (int) Math.floor(minutes * 0.2);
                energies.set(i, Math.min(100, energy + gained));
                updates.set(i, now);
            } else if (minutes > 0) {
                updates.set(i, now);
            }
        }

        scooterRepository.saveEnergies(station, energies, updates);
    }
}
