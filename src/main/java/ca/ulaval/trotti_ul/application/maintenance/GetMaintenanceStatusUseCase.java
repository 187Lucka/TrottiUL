package ca.ulaval.trotti_ul.application.maintenance;

import java.util.List;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.maintenance.Maintenance;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRepository;
import ca.ulaval.trotti_ul.domain.station.Station;
import ca.ulaval.trotti_ul.domain.station.StationRepository;

public class GetMaintenanceStatusUseCase {

    private final MaintenanceRepository maintenanceRepository;
    private final StationRepository stationRepository;

    public GetMaintenanceStatusUseCase(MaintenanceRepository maintenanceRepository,
                                       StationRepository stationRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.stationRepository = stationRepository;
    }

    public List<Maintenance> handleGetAll() {
        return maintenanceRepository.findAllActive();
    }

    public Optional<Maintenance> handleGetByStation(String stationLocationName) {
        return stationRepository.findByLocationName(stationLocationName)
                .map(Station::location)
                .flatMap(maintenanceRepository::findActiveByStation);
    }
}
