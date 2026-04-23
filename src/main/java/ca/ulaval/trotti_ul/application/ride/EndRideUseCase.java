package ca.ulaval.trotti_ul.application.ride;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.billing.RideBillingService;
import ca.ulaval.trotti_ul.domain.pass.AccessPlanService;
import ca.ulaval.trotti_ul.domain.pass.EffectivePass;
import ca.ulaval.trotti_ul.domain.pass.Pass;
import ca.ulaval.trotti_ul.domain.pass.PassRepository;
import ca.ulaval.trotti_ul.domain.ride.NoActiveRideException;
import ca.ulaval.trotti_ul.domain.ride.NoValidPassForUnlockException;
import ca.ulaval.trotti_ul.domain.ride.Ride;
import ca.ulaval.trotti_ul.domain.ride.RidePolicy;
import ca.ulaval.trotti_ul.domain.ride.RideRepository;
import ca.ulaval.trotti_ul.domain.scooter.ScooterEnergyService;
import ca.ulaval.trotti_ul.domain.scooter.ScooterReservation;
import ca.ulaval.trotti_ul.domain.station.Station;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.StationUnderMaintenanceException;
import ca.ulaval.trotti_ul.domain.station.UnknownStationException;
import ca.ulaval.trotti_ul.domain.account.AccountNotFoundException;

public class EndRideUseCase {

    private final PassRepository passRepository;
    private final AccessPlanService accessPlanService;
    private final RideRepository rideRepository;
    private final ScooterReservation scooterReservation;
    private final ScooterEnergyService scooterEnergyService;
    private final StationRepository stationRepository;
    private final AccountRepository accountRepository;
    private final RideBillingService rideBillingService;
    private final RidePolicy ridePolicy;
    private final Clock clock;

    public EndRideUseCase(PassRepository passRepository,
                          AccessPlanService accessPlanService,
                          RideRepository rideRepository,
                          ScooterReservation scooterReservation,
                          ScooterEnergyService scooterEnergyService,
                          StationRepository stationRepository,
                          AccountRepository accountRepository,
                          RideBillingService rideBillingService,
                          RidePolicy ridePolicy,
                          Clock clock) {
        this.passRepository = passRepository;
        this.accessPlanService = accessPlanService;
        this.rideRepository = rideRepository;
        this.scooterReservation = scooterReservation;
        this.scooterEnergyService = scooterEnergyService;
        this.stationRepository = stationRepository;
        this.accountRepository = accountRepository;
        this.rideBillingService = rideBillingService;
        this.ridePolicy = ridePolicy;
        this.clock = clock;
    }

    public Ride handle(String accountIdString, String stationLocationName, int slotNumber) {
        AccountId accountId = AccountId.fromString(accountIdString);
        Account account = loadAccount(accountId);
        StationLocation stationLocation = resolveStation(stationLocationName);

        if (stationRepository.isUnderMaintenance(stationLocation)) {
            throw new StationUnderMaintenanceException(stationLocation.value());
        }

        LocalDate today = LocalDate.now(clock);
        EffectivePass plan = requirePlan(account, today);
        Pass pass = plan.billable() ? passRepository.findByAccountAndSemester(accountId, plan.semesterCode()).orElse(null) : null;
        Ride activeRide = loadActiveRide(accountId);

        Instant now = Instant.now(clock);
        scooterEnergyService.applyRechargeIfAllowed(stationLocation);
        var result = activeRide.finishWithPolicy(
                stationLocation,
                now,
                ridePolicy,
                plan,
                rideRepository.findByAccountId(accountId)
        );

        scooterReservation.returnScooter(stationLocation, slotNumber, result.remainingEnergy());
        rideRepository.completeRide(result.ride());
        billIfNeeded(result.extraChargeCents(), accountId, pass, result.ride());
        return result.ride();
    }

    private Account loadAccount(AccountId accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
    }

    private StationLocation resolveStation(String stationLocationName) {
        return stationRepository.findByLocationName(stationLocationName)
                .map(Station::location)
                .orElseThrow(() -> new UnknownStationException(stationLocationName));
    }

    private EffectivePass requirePlan(Account account, LocalDate today) {
        return accessPlanService.getEffectivePassFor(account, today)
                .orElseThrow(NoValidPassForUnlockException::new);
    }

    private Ride loadActiveRide(AccountId accountId) {
        return rideRepository.findActiveByAccountId(accountId)
                .orElseThrow(NoActiveRideException::new);
    }

    private void billIfNeeded(int extraChargeCents, AccountId accountId, Pass pass, Ride completed) {
        if (extraChargeCents > 0) {
            rideBillingService.chargeRideOverage(accountId, pass, extraChargeCents, completed.id().value().toString());
        }
    }
}
