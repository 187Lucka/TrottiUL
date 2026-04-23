package ca.ulaval.trotti_ul.domain.station;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class UnknownStationException extends BusinessException {
    public UnknownStationException(String location) {
        super("UNKNOWN_STATION", "Station not found: " + location);
    }
}
