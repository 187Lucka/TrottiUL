package ca.ulaval.trotti_ul.infrastructure.scooter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.ulaval.trotti_ul.domain.common.TechnicalException;
import ca.ulaval.trotti_ul.domain.scooter.ScooterEnergyRepository;
import ca.ulaval.trotti_ul.domain.scooter.ScooterInventory;
import ca.ulaval.trotti_ul.domain.scooter.ScooterReservation;
import ca.ulaval.trotti_ul.domain.scooter.ScooterSnapshot;
import ca.ulaval.trotti_ul.domain.station.ScooterUnavailableException;
import ca.ulaval.trotti_ul.domain.station.StationLocation;

public class InMemoryScooterRepository implements ScooterReservation, ScooterInventory, ScooterEnergyRepository {

    private final Map<StationLocation, List<Integer>> stationEnergies = new ConcurrentHashMap<>();
    private final Map<StationLocation, List<java.time.Instant>> lastUpdated = new ConcurrentHashMap<>();

    public InMemoryScooterRepository() {
        loadInitialDistribution();
    }

    @Override
    public List<ScooterSnapshot> findByStation(StationLocation station) {
        List<Integer> energies = stationEnergies.getOrDefault(station, List.of());
        List<ScooterSnapshot> snapshots = new ArrayList<>(energies.size());
        for (int i = 0; i < energies.size(); i++) {
            int energy = energies.get(i);
            snapshots.add(new ScooterSnapshot(
                    station.value() + "-S" + (i + 1),
                    station,
                    i + 1,
                    energy,
                    energy > 0
            ));
        }
        return snapshots;
    }

    @Override
    public int countByStation(StationLocation station) {
        return (int) stationEnergies.getOrDefault(station, List.of()).stream()
                .filter(e -> e > 0)
                .count();
    }

    @Override
    public int reserveScooter(StationLocation station, int slotNumber) {
        List<Integer> energies = stationEnergies.get(station);
        if (energies == null) {
            throw new ScooterUnavailableException("Station not found: " + station.value());
        }
        int index = slotNumber - 1;
        if (index < 0 || index >= energies.size()) {
            throw new ScooterUnavailableException("No scooter at slot " + slotNumber + " for station " + station.value());
        }
        int energy = energies.get(index);
        if (energy <= 0) {
            throw new ScooterUnavailableException("No scooter at slot " + slotNumber + " for station " + station.value());
        }
        if (energy <= 15) {
            throw new ScooterUnavailableException("Scooter energy too low at slot " + slotNumber + " for station " + station.value());
        }
        energies.set(index, 0);
        lastUpdated.get(station).set(index, java.time.Instant.now());
        return energy;
    }

    @Override
    public void returnScooter(StationLocation station, int slotNumber, int energyPercent) {
        List<Integer> energies = stationEnergies.get(station);
        if (energies == null) {
            throw new ScooterUnavailableException("Station not found: " + station.value());
        }
        int index = slotNumber - 1;
        if (index < 0 || index >= energies.size()) {
            throw new ScooterUnavailableException("Invalid slot number " + slotNumber + " for station " + station.value());
        }
        if (energies.get(index) > 0) {
            throw new ScooterUnavailableException("Slot " + slotNumber + " already occupied at station " + station.value());
        }
        int clamped = Math.max(0, Math.min(100, energyPercent));
        energies.set(index, clamped);
        lastUpdated.get(station).set(index, java.time.Instant.now());
    }

    @Override
    public Optional<ScooterSnapshot> findByStationAndSlot(StationLocation station, int slotNumber) {
        List<Integer> energies = stationEnergies.get(station);
        if (energies == null) {
            return Optional.empty();
        }
        int index = slotNumber - 1;
        if (index < 0 || index >= energies.size()) {
            return Optional.empty();
        }
        int energy = energies.get(index);
        if (energy <= 0) {
            return Optional.empty();
        }
        return Optional.of(new ScooterSnapshot(
                station.value() + "-S" + slotNumber,
                station,
                slotNumber,
                energy,
                true
        ));
    }

    @Override
    public int removeForTransfer(StationLocation station, int slotNumber) {
        List<Integer> energies = stationEnergies.get(station);
        if (energies == null) {
            throw new ScooterUnavailableException("Station not found: " + station.value());
        }
        int index = slotNumber - 1;
        if (index < 0 || index >= energies.size()) {
            throw new ScooterUnavailableException("No scooter at slot " + slotNumber + " for station " + station.value());
        }
        int energy = energies.get(index);
        if (energy <= 0) {
            throw new ScooterUnavailableException("No scooter at slot " + slotNumber + " for station " + station.value());
        }
        energies.set(index, 0);
        lastUpdated.get(station).set(index, java.time.Instant.now());
        return energy;
    }

    @Override
    public boolean isSlotEmpty(StationLocation station, int slotNumber) {
        List<Integer> energies = stationEnergies.get(station);
        if (energies == null) {
            return true;
        }
        int index = slotNumber - 1;
        if (index < 0 || index >= energies.size()) {
            return true;
        }
        return energies.get(index) <= 0;
    }

    @Override
    public List<Integer> getEnergies(StationLocation station) {
        List<Integer> energies = stationEnergies.get(station);
        if (energies == null) {
            return null;
        }
        return new ArrayList<>(energies);
    }

    @Override
    public List<java.time.Instant> getLastUpdated(StationLocation station) {
        List<java.time.Instant> updates = lastUpdated.get(station);
        if (updates == null) {
            return null;
        }
        return new ArrayList<>(updates);
    }

    @Override
    public void saveEnergies(StationLocation station, List<Integer> energies, List<java.time.Instant> updates) {
        if (energies == null || updates == null || energies.size() != updates.size()) {
            return;
        }
        stationEnergies.put(station, new ArrayList<>(energies));
        lastUpdated.put(station, new ArrayList<>(updates));
    }

    private void loadInitialDistribution() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("stations.json")) {
            List<Map<String, Object>> raw = mapper.readValue(in, new TypeReference<>() {});
            for (Map<String, Object> station : raw) {
                String locStr = (String) station.get("location");
                StationLocation location = StationLocation.fromString(locStr);
                int capacity = (Integer) station.get("capacity");
                int initialCount = Math.max(1, (int) Math.ceil(capacity * 0.8));
                List<Integer> energies = new ArrayList<>();
                List<java.time.Instant> updated = new ArrayList<>();
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                for (int i = 0; i < capacity; i++) {
                    if (i < initialCount) {
                        energies.add(rnd.nextInt(0, 101));
                    } else {
                        energies.add(0);
                    }
                    updated.add(java.time.Instant.now());
                }
                stationEnergies.put(location, energies);
                lastUpdated.put(location, updated);
            }
        } catch (IOException e) {
            throw new TechnicalException("Failed to load initial scooters", e);
        }
    }

}
