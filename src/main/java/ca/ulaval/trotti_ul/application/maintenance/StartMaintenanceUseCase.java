package ca.ulaval.trotti_ul.application.maintenance;

import java.time.Clock;
import java.time.Instant;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.maintenance.Maintenance;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceAlreadyInProgressException;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRepository;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequest;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestId;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestNotFoundException;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestNotPendingException;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestRepository;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestStatus;
import ca.ulaval.trotti_ul.domain.maintenance.NotATechnicianException;
import ca.ulaval.trotti_ul.domain.station.Station;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.StationStatus;
import ca.ulaval.trotti_ul.domain.station.UnknownStationException;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;

public class StartMaintenanceUseCase {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final StationRepository stationRepository;
    private final TechnicianRepository technicianRepository;
    private final Clock clock;

    public StartMaintenanceUseCase(MaintenanceRepository maintenanceRepository,
                                   MaintenanceRequestRepository maintenanceRequestRepository,
                                   StationRepository stationRepository,
                                   TechnicianRepository technicianRepository,
                                   Clock clock) {
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.stationRepository = stationRepository;
        this.technicianRepository = technicianRepository;
        this.clock = clock;
    }

    public Maintenance handleFromRequest(String accountIdString, String maintenanceRequestIdString) {
        AccountId accountId = AccountId.fromString(accountIdString);
        Technician technician = requireTechnician(accountId);

        MaintenanceRequestId requestId = MaintenanceRequestId.fromString(maintenanceRequestIdString);
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(MaintenanceRequestNotFoundException::new);

        if (maintenanceRequest.status() != MaintenanceRequestStatus.PENDING) {
            throw new MaintenanceRequestNotPendingException();
        }

        StationLocation stationLocation = maintenanceRequest.stationLocation();
        resolveStation(stationLocation.value());

        maintenanceRequest.markInProgress();
        maintenanceRequestRepository.save(maintenanceRequest);

        return startMaintenance(technician, stationLocation, maintenanceRequest);
    }

    private Maintenance startMaintenance(Technician technician,
                                         StationLocation stationLocation,
                                         MaintenanceRequest associatedRequest) {
        maintenanceRepository.findActiveByStation(stationLocation)
                .ifPresent(m -> {
                    throw new MaintenanceAlreadyInProgressException(stationLocation.value());
                });

        stationRepository.updateStatus(stationLocation, StationStatus.UNDER_MAINTENANCE);

        Instant now = Instant.now(clock);
        Maintenance maintenance = Maintenance.start(
                stationLocation,
                technician.id(),
                associatedRequest != null ? associatedRequest.id() : null,
                now
        );
        maintenanceRepository.save(maintenance);

        return maintenance;
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
