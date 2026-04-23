package ca.ulaval.trotti_ul.application.station;

import java.util.List;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.scooter.ScooterEnergyService;
import ca.ulaval.trotti_ul.domain.scooter.ScooterInventory;
import ca.ulaval.trotti_ul.domain.scooter.ScooterSnapshot;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.StationSnapshot;

public class GetStationDetailsUseCase {

    private final StationRepository stationRepository;
    private final ScooterInventory scooterInventory;
    private final ScooterEnergyService scooterEnergyService;

    public GetStationDetailsUseCase(StationRepository stationRepository,
                                    ScooterInventory scooterInventory,
                                    ScooterEnergyService scooterEnergyService) {
        this.stationRepository = stationRepository;
        this.scooterInventory = scooterInventory;
        this.scooterEnergyService = scooterEnergyService;
    }

    public Optional<StationDetails> handle(String location) {
        return stationRepository.findByLocationName(location)
                .map(station -> {
                    scooterEnergyService.applyRechargeIfAllowed(station.location());
                    return new StationDetails(station.snapshot(), scooterInventory.findByStation(station.location()));
                });
    }

    public record StationDetails(StationSnapshot station, List<ScooterSnapshot> scooters) { }
}
