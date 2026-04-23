package ca.ulaval.trotti_ul.application.maintenance;

import java.time.Clock;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.maintenance.Maintenance;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceDomainService;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRepository;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestId;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestNotLinkedException;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestRepository;
import ca.ulaval.trotti_ul.domain.maintenance.NoMaintenanceInProgressException;
import ca.ulaval.trotti_ul.domain.maintenance.NotATechnicianException;
import ca.ulaval.trotti_ul.domain.maintenance.TechnicianTruckRepository;
import ca.ulaval.trotti_ul.domain.station.Station;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.UnknownStationException;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;

public class EndMaintenanceUseCase {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final StationRepository stationRepository;
    private final TechnicianRepository technicianRepository;
    private final TechnicianTruckRepository technicianTruckRepository;
    private final MaintenanceDomainService maintenanceDomainService;
    private final Clock clock;

    public EndMaintenanceUseCase(MaintenanceRepository maintenanceRepository,
                                  MaintenanceRequestRepository maintenanceRequestRepository,
                                  StationRepository stationRepository,
                                  TechnicianRepository technicianRepository,
                                  TechnicianTruckRepository technicianTruckRepository,
                                  MaintenanceDomainService maintenanceDomainService,
                                  Clock clock) {
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.stationRepository = stationRepository;
        this.technicianRepository = technicianRepository;
        this.technicianTruckRepository = technicianTruckRepository;
        this.maintenanceDomainService = maintenanceDomainService;
        this.clock = clock;
    }

    public Maintenance handle(String accountIdString, String stationLocationName) {
        AccountId accountId = AccountId.fromString(accountIdString);
        Technician technician = requireTechnician(accountId);
        StationLocation stationLocation = resolveStation(stationLocationName);

        Maintenance maintenance = maintenanceRepository.findActiveByStation(stationLocation)
                .orElseThrow(() -> new NoMaintenanceInProgressException(stationLocation.value()));

        return completeMaintenance(technician, maintenance);
    }

    public Maintenance handleFromRequest(String accountIdString, String maintenanceRequestIdString) {
        AccountId accountId = AccountId.fromString(accountIdString);
        Technician technician = requireTechnician(accountId);

        MaintenanceRequestId requestId = MaintenanceRequestId.fromString(maintenanceRequestIdString);

        Maintenance maintenance = maintenanceRepository.findActiveByRequestId(requestId)
                .orElseThrow(MaintenanceRequestNotLinkedException::new);

        return completeMaintenance(technician, maintenance);
    }

    private Maintenance completeMaintenance(Technician technician, Maintenance maintenance) {
        return maintenanceDomainService.complete(maintenance, technician.id());
    }

    private Technician requireTechnician(AccountId accountId) {
        return technicianRepository.findByAccountId(accountId)
                .filter(Technician::isActive)
                .orElseThrow(NotATechnicianException::new);
    }

    private StationLocation resolveStation(String stationLocationName) {
        return stationRepository.findByLocationName(stationLocationName)
                .map(Station::location)
                .orElseThrow(() -> new UnknownStationException(stationLocationName));
    }
}
