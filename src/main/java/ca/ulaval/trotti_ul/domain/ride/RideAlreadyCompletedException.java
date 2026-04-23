package ca.ulaval.trotti_ul.domain.ride;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class RideAlreadyCompletedException extends BusinessException {
    public RideAlreadyCompletedException() {
        super("RIDE_ALREADY_COMPLETED", "Ride is already completed");
    }
}
