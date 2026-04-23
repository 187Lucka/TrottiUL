package ca.ulaval.trotti_ul.domain.maintenance;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class TruckNotEmptyException extends BusinessException {

    public TruckNotEmptyException() {
        super("TRUCK_NOT_EMPTY", "Cannot end maintenance while scooters are still in the truck");
    }
}
