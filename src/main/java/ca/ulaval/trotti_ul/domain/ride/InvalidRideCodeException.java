package ca.ulaval.trotti_ul.domain.ride;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class InvalidRideCodeException extends BusinessException {
    public InvalidRideCodeException() {
        super("INVALID_RIDE_CODE", "Ride code is invalid or expired");
    }
}
