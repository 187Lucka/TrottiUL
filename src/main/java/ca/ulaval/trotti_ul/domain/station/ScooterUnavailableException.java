package ca.ulaval.trotti_ul.domain.station;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class ScooterUnavailableException extends BusinessException {
    public ScooterUnavailableException(String message) {
        super("SCOOTER_UNAVAILABLE", message);
    }
}
