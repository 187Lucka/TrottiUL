package ca.ulaval.trotti_ul.domain.station;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class StationUnderMaintenanceException extends BusinessException {

    public StationUnderMaintenanceException(String stationLocation) {
        super("STATION_UNDER_MAINTENANCE", "Station " + stationLocation + " is under maintenance");
    }
}
