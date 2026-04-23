package ca.ulaval.trotti_ul.domain.maintenance;

import java.util.Optional;

import ca.ulaval.trotti_ul.domain.technician.TechnicianId;

public interface TechnicianTruckRepository {

    void save(TechnicianTruck truck);

    Optional<TechnicianTruck> findByTechnicianId(TechnicianId technicianId);

    TechnicianTruck getOrCreate(TechnicianId technicianId);
}
