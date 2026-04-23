package ca.ulaval.trotti_ul.infrastructure.maintenance;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequest;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestId;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestRepository;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestStatus;
import ca.ulaval.trotti_ul.domain.station.StationLocation;

public class InMemoryMaintenanceRequestRepository implements MaintenanceRequestRepository {

    private final Map<MaintenanceRequestId, MaintenanceRequest> byId = new ConcurrentHashMap<>();

    @Override
    public void save(MaintenanceRequest request) {
        byId.put(request.id(), request);
    }

    @Override
    public Optional<MaintenanceRequest> findById(MaintenanceRequestId id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<MaintenanceRequest> findByStation(StationLocation stationLocation) {
        return byId.values().stream()
                .filter(r -> r.stationLocation().equals(stationLocation))
                .toList();
    }

    @Override
    public List<MaintenanceRequest> findPendingByStation(StationLocation stationLocation) {
        return byId.values().stream()
                .filter(r -> r.stationLocation().equals(stationLocation))
                .filter(r -> r.status() == MaintenanceRequestStatus.PENDING)
                .toList();
    }

    @Override
    public List<MaintenanceRequest> findAll() {
        return List.copyOf(byId.values());
    }
}
