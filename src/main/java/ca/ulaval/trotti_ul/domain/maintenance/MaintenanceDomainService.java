package ca.ulaval.trotti_ul.domain.maintenance;

import java.time.Clock;
import java.time.Instant;

import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.StationStatus;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianId;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;
import ca.ulaval.trotti_ul.domain.maintenance.NotATechnicianException;

/**
 * Service de domaine pour encapsuler les règles de clôture de maintenance
 * (camion vide, statut de station, mise à jour de la demande).
 */
public class MaintenanceDomainService {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final StationRepository stationRepository;
    private final TechnicianRepository technicianRepository;
    private final TechnicianTruckRepository technicianTruckRepository;
    private final Clock clock;

    public MaintenanceDomainService(MaintenanceRepository maintenanceRepository,
                                    MaintenanceRequestRepository maintenanceRequestRepository,
                                    StationRepository stationRepository,
                                    TechnicianRepository technicianRepository,
                                    TechnicianTruckRepository technicianTruckRepository,
                                    Clock clock) {
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.stationRepository = stationRepository;
        this.technicianRepository = technicianRepository;
        this.technicianTruckRepository = technicianTruckRepository;
        this.clock = clock;
    }

    public Maintenance complete(Maintenance maintenance, TechnicianId technicianId) {
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(NotATechnicianException::new);

        TechnicianTruck truck = technicianTruckRepository.getOrCreate(technician.id());
        truck.requireEmpty();
        StationLocation stationLocation = maintenance.stationLocation();

        Instant now = Instant.now(clock);
        maintenance.complete(now);
        maintenanceRepository.save(maintenance);

        maintenance.requestId().ifPresent(requestId -> {
            maintenanceRequestRepository.findById(requestId).ifPresent(request -> {
                request.markCompleted();
                maintenanceRequestRepository.save(request);
            });
        });

        stationRepository.updateStatus(stationLocation, StationStatus.OPERATIONAL);
        technicianTruckRepository.save(truck);

        return maintenance;
    }
}
