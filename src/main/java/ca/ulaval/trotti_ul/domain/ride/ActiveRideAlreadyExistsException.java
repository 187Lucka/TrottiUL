package ca.ulaval.trotti_ul.domain.ride;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class ActiveRideAlreadyExistsException extends BusinessException {
    public ActiveRideAlreadyExistsException() {
        super("ACTIVE_RIDE_EXISTS", "An active ride already exists for this account");
    }
}
