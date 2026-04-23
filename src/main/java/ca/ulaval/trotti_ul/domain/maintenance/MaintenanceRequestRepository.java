package ca.ulaval.trotti_ul.domain.maintenance;

import java.util.List;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.station.StationLocation;

public interface MaintenanceRequestRepository {

    void save(MaintenanceRequest request);

    Optional<MaintenanceRequest> findById(MaintenanceRequestId id);

    List<MaintenanceRequest> findByStation(StationLocation stationLocation);

    List<MaintenanceRequest> findPendingByStation(StationLocation stationLocation);

    List<MaintenanceRequest> findAll();
}
