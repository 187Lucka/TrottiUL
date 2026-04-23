package ca.ulaval.trotti_ul.application.ride;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountNotFoundException;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.pass.AccessPlanService;
import ca.ulaval.trotti_ul.domain.ride.ActiveRideAlreadyExistsException;
import ca.ulaval.trotti_ul.domain.ride.GenerateCode;
import ca.ulaval.trotti_ul.domain.ride.InvalidRideCodeException;
import ca.ulaval.trotti_ul.domain.ride.NoValidPassForUnlockException;
import ca.ulaval.trotti_ul.domain.ride.Ride;
import ca.ulaval.trotti_ul.domain.ride.RideCodeRepository;
import ca.ulaval.trotti_ul.domain.ride.RideRepository;
import ca.ulaval.trotti_ul.domain.scooter.ScooterEnergyService;
import ca.ulaval.trotti_ul.domain.scooter.ScooterReservation;
import ca.ulaval.trotti_ul.domain.station.Station;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.StationUnderMaintenanceException;
import ca.ulaval.trotti_ul.domain.station.UnknownStationException;

public class StartRideUseCase {

    private final AccessPlanService accessPlanService;
    private final RideCodeRepository rideCodeRepository;
    private final RideRepository rideRepository;
    private final ScooterReservation scooterReservation;
    private final ScooterEnergyService scooterEnergyService;
    private final StationRepository stationRepository;
    private final AccountRepository accountRepository;
    private final Clock clock;

    public StartRideUseCase(AccessPlanService accessPlanService,
                            RideCodeRepository rideCodeRepository,
                            RideRepository rideRepository,
                            ScooterReservation scooterReservation,
                            ScooterEnergyService scooterEnergyService,
                            StationRepository stationRepository,
                            AccountRepository accountRepository,
                            Clock clock) {
        this.accessPlanService = accessPlanService;
        this.rideCodeRepository = rideCodeRepository;
        this.rideRepository = rideRepository;
        this.scooterReservation = scooterReservation;
        this.scooterEnergyService = scooterEnergyService;
        this.stationRepository = stationRepository;
        this.accountRepository = accountRepository;
        this.clock = clock;
    }

    public void handle(String accountIdString, String codeValue, String stationLocationName, int slotNumber) {
        AccountId accountId = AccountId.fromString(accountIdString);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
        StationLocation location = stationRepository.findByLocationName(stationLocationName)
                .map(Station::location)
                .orElseThrow(() -> new UnknownStationException(stationLocationName));

        if (stationRepository.isUnderMaintenance(location)) {
            throw new StationUnderMaintenanceException(location.value());
        }

        LocalDate today = LocalDate.now(clock);
        accessPlanService.getEffectivePassFor(account, today)
                .orElseThrow(NoValidPassForUnlockException::new);

        GenerateCode code = rideCodeRepository.findByAccountId(accountId)
                .orElseThrow(InvalidRideCodeException::new);
        Instant now = Instant.now(clock);
        if (code.isExpired(now) || !code.value().equals(codeValue)) {
            throw new InvalidRideCodeException();
        }

        rideRepository.findActiveByAccountId(accountId)
                .ifPresent(r -> { throw new ActiveRideAlreadyExistsException(); });

        scooterEnergyService.applyRechargeIfAllowed(location);
        int startEnergy = scooterReservation.reserveScooter(location, slotNumber);

        rideCodeRepository.delete(accountId);

        Ride ride = Ride.start(accountId, location, now, startEnergy);
        rideRepository.saveActive(ride);
    }
}
