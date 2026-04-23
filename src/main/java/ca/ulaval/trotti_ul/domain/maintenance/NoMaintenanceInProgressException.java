package ca.ulaval.trotti_ul.domain.maintenance;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class NoMaintenanceInProgressException extends BusinessException {

    public NoMaintenanceInProgressException(String stationLocation) {
        super("NO_MAINTENANCE_IN_PROGRESS", "No maintenance in progress for station " + stationLocation);
    }
}
