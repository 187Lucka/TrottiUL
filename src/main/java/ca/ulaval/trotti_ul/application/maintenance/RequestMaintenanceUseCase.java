package ca.ulaval.trotti_ul.application.maintenance;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequest;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestRepository;
import ca.ulaval.trotti_ul.domain.notification.EmailTemplateId;
import ca.ulaval.trotti_ul.domain.notification.TemplatedEmailService;
import ca.ulaval.trotti_ul.domain.station.Station;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.UnknownStationException;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;

public class RequestMaintenanceUseCase {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final StationRepository stationRepository;
    private final TechnicianRepository technicianRepository;
    private final AccountRepository accountRepository;
    private final TemplatedEmailService emailService;
    private final Clock clock;

    public RequestMaintenanceUseCase(MaintenanceRequestRepository maintenanceRequestRepository,
            StationRepository stationRepository,
            TechnicianRepository technicianRepository,
            AccountRepository accountRepository,
            TemplatedEmailService emailService,
            Clock clock) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.stationRepository = stationRepository;
        this.technicianRepository = technicianRepository;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
        this.clock = clock;
    }

    public MaintenanceRequest handle(String stationLocationName, String accountIdString, String reason) {
        StationLocation stationLocation = resolveStation(stationLocationName);
        AccountId requestedBy = accountIdString != null ? AccountId.fromString(accountIdString) : null;

        Instant now = Instant.now(clock);
        MaintenanceRequest request = MaintenanceRequest.create(stationLocation, requestedBy, reason, now);
        maintenanceRequestRepository.save(request);

        notifyTechnicians(stationLocation, reason);

        return request;
    }

    private StationLocation resolveStation(String stationLocationName) {
        return stationRepository.findByLocationName(stationLocationName)
                .map(Station::location)
                .orElseThrow(() -> new UnknownStationException(stationLocationName));
    }

    private void notifyTechnicians(StationLocation stationLocation, String reason) {
        List<Technician> technicians = technicianRepository.findAllActive();
        if (technicians.isEmpty()) {
            return;
        }

        List<AccountId> accountIds = technicians.stream()
                .map(Technician::accountId)
                .toList();
        List<Account> accounts = accountRepository.findByIds(accountIds);

        for (Account account : accounts) {
            emailService.send(
                    account.email().toString(),
                    EmailTemplateId.MAINTENANCE_REQUEST,
                    buildEmailVariables(stationLocation, reason));
        }
    }

    private Map<String, String> buildEmailVariables(StationLocation stationLocation, String reason) {
        return Map.of(
                "station", stationLocation.value(),
                "reason", reason == null ? "" : reason);
    }
}
