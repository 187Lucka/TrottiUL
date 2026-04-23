package ca.ulaval.trotti_ul.domain.maintenance;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class MaintenanceAlreadyInProgressException extends BusinessException {

    public MaintenanceAlreadyInProgressException(String stationLocation) {
        super("MAINTENANCE_ALREADY_IN_PROGRESS", "Maintenance already in progress for station " + stationLocation);
    }
}
