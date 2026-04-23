package ca.ulaval.trotti_ul.infrastructure.maintenance;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ca.ulaval.trotti_ul.domain.maintenance.TechnicianTruck;
import ca.ulaval.trotti_ul.domain.maintenance.TechnicianTruckRepository;
import ca.ulaval.trotti_ul.domain.technician.TechnicianId;

public class InMemoryTechnicianTruckRepository implements TechnicianTruckRepository {

    private final Map<TechnicianId, TechnicianTruck> byTechnicianId = new ConcurrentHashMap<>();

    @Override
    public void save(TechnicianTruck truck) {
        byTechnicianId.put(truck.technicianId(), truck);
    }

    @Override
    public Optional<TechnicianTruck> findByTechnicianId(TechnicianId technicianId) {
        return Optional.ofNullable(byTechnicianId.get(technicianId));
    }

    @Override
    public TechnicianTruck getOrCreate(TechnicianId technicianId) {
        return byTechnicianId.computeIfAbsent(technicianId, TechnicianTruck::new);
    }
}
