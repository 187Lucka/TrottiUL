package ca.ulaval.trotti_ul.domain.ride;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.pass.EffectivePass;

/**
 * Regroupe les règles de calcul liées à un trajet (énergie, surcoûts).
 */
public class RidePolicy {

    private static final long FREE_RIDE_SECONDS = 15 * 60L;
    private static final int DAILY_OVERAGE_CENTS = 500;

    private final Clock clock;

    public RidePolicy(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    public int remainingEnergy(Instant startTime, Instant endTime, int startEnergyPercent) {
        long rideSeconds = Duration.between(startTime, endTime).getSeconds();
        long minutes = rideSeconds / 60;
        int energyConsumed = (int) Math.ceil(minutes * 0.5);
        return Math.max(0, startEnergyPercent - energyConsumed);
    }

    public int computeOverageCents(EffectivePass plan, AccountId accountId, LocalDate today, Iterable<Ride> rideHistory, long rideSeconds) {
        int dailyOverage = computeDailyOverage(plan, accountId, today, rideHistory, rideSeconds);
        int perRideOverage = computePerRideOverage(rideSeconds);
        return dailyOverage + perRideOverage;
    }

    private int computeDailyOverage(EffectivePass plan, AccountId accountId, LocalDate today, Iterable<Ride> rideHistory, long rideSeconds) {
        long thresholdSeconds = plan.dailyUnlimitedMinutes() * 60L;
        long todaysSeconds = 0;
        for (Ride r : rideHistory) {
            if (r.endTime() != null && r.endTime().atZone(clock.getZone()).toLocalDate().equals(today)) {
                todaysSeconds += r.durationSeconds();
            }
        }
        long beforeThisRide = todaysSeconds;
        long afterThisRide = todaysSeconds + rideSeconds;
        boolean crossedThreshold = beforeThisRide <= thresholdSeconds && afterThisRide > thresholdSeconds;
        return crossedThreshold ? DAILY_OVERAGE_CENTS : 0;
    }

    private int computePerRideOverage(long rideSeconds) {
        if (rideSeconds <= FREE_RIDE_SECONDS) {
            return 0;
        }
        long overageSeconds = rideSeconds - FREE_RIDE_SECONDS;
        long overageMinutesRounded = overageSeconds / 60 + (overageSeconds % 60 > 14 ? 1 : 0);
        return (int) Math.ceil(overageMinutesRounded * 7.5);
    }

    public java.time.ZoneId zone() {
        return clock.getZone();
    }
}
