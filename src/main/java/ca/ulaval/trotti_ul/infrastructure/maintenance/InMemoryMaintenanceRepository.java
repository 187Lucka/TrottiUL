package ca.ulaval.trotti_ul.infrastructure.maintenance;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ca.ulaval.trotti_ul.domain.maintenance.Maintenance;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceId;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestId;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRepository;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.technician.TechnicianId;

public class InMemoryMaintenanceRepository implements MaintenanceRepository {

    private final Map<MaintenanceId, Maintenance> byId = new ConcurrentHashMap<>();

    @Override
    public void save(Maintenance maintenance) {
        byId.put(maintenance.id(), maintenance);
    }

    @Override
    public Optional<Maintenance> findById(MaintenanceId id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<Maintenance> findActiveByStation(StationLocation stationLocation) {
        return byId.values().stream()
                .filter(m -> m.stationLocation().equals(stationLocation))
                .filter(Maintenance::isActive)
                .findFirst();
    }

    @Override
    public Optional<Maintenance> findActiveByRequestId(MaintenanceRequestId requestId) {
        return byId.values().stream()
                .filter(m -> m.requestId().isPresent())
                .filter(m -> m.requestId().get().equals(requestId))
                .filter(Maintenance::isActive)
                .findFirst();
    }

    @Override
    public List<Maintenance> findByStation(StationLocation stationLocation) {
        return byId.values().stream()
                .filter(m -> m.stationLocation().equals(stationLocation))
                .toList();
    }

    @Override
    public List<Maintenance> findByTechnician(TechnicianId technicianId) {
        return byId.values().stream()
                .filter(m -> m.technicianId().equals(technicianId))
                .toList();
    }

    @Override
    public List<Maintenance> findAllActive() {
        return byId.values().stream()
                .filter(Maintenance::isActive)
                .toList();
    }
}
