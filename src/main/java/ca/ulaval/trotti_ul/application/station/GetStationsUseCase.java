package ca.ulaval.trotti_ul.application.station;

import java.util.List;

import ca.ulaval.trotti_ul.domain.scooter.ScooterEnergyService;
import ca.ulaval.trotti_ul.domain.scooter.ScooterInventory;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.StationSnapshot;

public class GetStationsUseCase {

    private final StationRepository stationRepository;
    private final ScooterInventory scooterInventory;
    private final ScooterEnergyService scooterEnergyService;

    public GetStationsUseCase(StationRepository stationRepository,
                              ScooterInventory scooterInventory,
                              ScooterEnergyService scooterEnergyService) {
        this.stationRepository = stationRepository;
        this.scooterInventory = scooterInventory;
        this.scooterEnergyService = scooterEnergyService;
    }

    public List<StationSnapshotWithCount> handle() {
        return stationRepository.findAll().stream()
                .map(station -> {
                    scooterEnergyService.applyRechargeIfAllowed(station.location());
                    return new StationSnapshotWithCount(station.snapshot(), scooterInventory.countByStation(station.location()));
                })
                .toList();
    }

    public record StationSnapshotWithCount(StationSnapshot snapshot, int scootersCount) { }
}
