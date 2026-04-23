package ca.ulaval.trotti_ul.domain.ride;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.ride.RideAlreadyCompletedException;
import ca.ulaval.trotti_ul.domain.pass.EffectivePass;

public class Ride {

    private final RideId id;
    private final AccountId accountId;
    private final StationLocation startStation;
    private final Instant startTime;
    private final StationLocation endStation;
    private final Instant endTime;
    private final long durationSeconds;
    private final int extraChargeCents;
    private final int startEnergyPercent;
    private final int endEnergyPercent;

    private Ride(RideId id,
                 AccountId accountId,
                 StationLocation startStation,
                 Instant startTime,
                 StationLocation endStation,
                 Instant endTime,
                 long durationSeconds,
                 int extraChargeCents,
                 int startEnergyPercent,
                 int endEnergyPercent) {
        this.id = Objects.requireNonNull(id);
        this.accountId = Objects.requireNonNull(accountId);
        this.startStation = Objects.requireNonNull(startStation);
        this.startTime = Objects.requireNonNull(startTime);
        this.endStation = endStation;
        this.endTime = endTime;
        this.durationSeconds = durationSeconds;
        this.extraChargeCents = extraChargeCents;
        this.startEnergyPercent = startEnergyPercent;
        this.endEnergyPercent = endEnergyPercent;
    }

    public static Ride start(AccountId accountId, StationLocation startStation, Instant startTime, int startEnergyPercent) {
        return new Ride(RideId.newId(), accountId, startStation, startTime, null, null, 0, 0, startEnergyPercent, 0);
    }

    /**
     * Termine un trajet en appliquant la policy (calcul surcoût/énergie) et renvoie l'état complet.
     */
    public RideWithPolicyResult finishWithPolicy(StationLocation endStation,
                                                 Instant endTime,
                                                 RidePolicy policy,
                                                 EffectivePass plan,
                                                 Iterable<Ride> rideHistory) {
        if (!isActive()) {
            throw new RideAlreadyCompletedException();
        }
        long rideSeconds = Duration.between(startTime, endTime).getSeconds();
        int remainingEnergy = policy.remainingEnergy(startTime, endTime, startEnergyPercent);
        int extraCharge = policy.computeOverageCents(plan, accountId, endTime.atZone(policy.zone()).toLocalDate(), rideHistory, rideSeconds);
        Ride completed = new Ride(id, accountId, startStation, startTime, endStation, endTime, rideSeconds, extraCharge, startEnergyPercent, remainingEnergy);
        return new RideWithPolicyResult(completed, extraCharge, remainingEnergy, endStation);
    }

    public Ride finish(StationLocation endStation, Instant endTime, int extraChargeCents, int endEnergyPercent) {
        if (!isActive()) {
            throw new RideAlreadyCompletedException();
        }
        long seconds = Duration.between(startTime, endTime).getSeconds();
        return new Ride(id, accountId, startStation, startTime, endStation, endTime, seconds, extraChargeCents, startEnergyPercent, endEnergyPercent);
    }

    public boolean isActive() {
        return endTime == null;
    }

    public RideId id() {
        return id;
    }

    public AccountId accountId() {
        return accountId;
    }

    public StationLocation startStation() {
        return startStation;
    }

    public Instant startTime() {
        return startTime;
    }

    public StationLocation endStation() {
        return endStation;
    }

    public Instant endTime() {
        return endTime;
    }

    public long durationSeconds() {
        return durationSeconds;
    }

    public long durationMinutes() {
        return durationSeconds / 60;
    }

    public int extraChargeCents() {
        return extraChargeCents;
    }

    public int startEnergyPercent() {
        return startEnergyPercent;
    }

    public int endEnergyPercent() {
        return endEnergyPercent;
    }
}
