package ca.ulaval.trotti_ul.domain.pass;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public record DailyTripDuration(int minutes) {

    private static final int MINIMUM_DURATION = 30;
    private static final int DURATION_INCREMENT = 10;

    public DailyTripDuration {
        if (minutes < MINIMUM_DURATION) {
            throw new ValidationException("Daily trip duration must be at least " + MINIMUM_DURATION + " minutes");
        }
        if (minutes % DURATION_INCREMENT != 0) {
            throw new ValidationException("Daily trip duration must be a multiple of " + DURATION_INCREMENT + " minutes");
        }
    }

    public static DailyTripDuration of(int minutes) {
        return new DailyTripDuration(minutes);
    }

    public static DailyTripDuration base() {
        return new DailyTripDuration(MINIMUM_DURATION);
    }

    public int extraMinutesOverBase() {
        return Math.max(0, minutes - MINIMUM_DURATION);
    }

    @Override
    public String toString() {
        return minutes + " minutes";
    }
}
