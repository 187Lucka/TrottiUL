package ca.ulaval.trotti_ul.domain.maintenance;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class ScooterNotInTruckException extends BusinessException {
    public ScooterNotInTruckException(String scooterId) {
        super("SCOOTER_NOT_IN_TRUCK", "Scooter " + scooterId + " is not in the truck");
    }
}
