package ca.ulaval.trotti_ul.domain.maintenance;

import java.util.List;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.technician.TechnicianId;

public interface MaintenanceRepository {

    void save(Maintenance maintenance);

    Optional<Maintenance> findById(MaintenanceId id);

    Optional<Maintenance> findActiveByStation(StationLocation stationLocation);

    Optional<Maintenance> findActiveByRequestId(MaintenanceRequestId requestId);

    List<Maintenance> findByStation(StationLocation stationLocation);

    List<Maintenance> findByTechnician(TechnicianId technicianId);

    List<Maintenance> findAllActive();
}
