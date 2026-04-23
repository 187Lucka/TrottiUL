package ca.ulaval.trotti_ul.domain.ride;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class NoActiveRideException extends BusinessException {
    public NoActiveRideException() {
        super("NO_ACTIVE_RIDE", "No active ride found for this account");
    }
}
