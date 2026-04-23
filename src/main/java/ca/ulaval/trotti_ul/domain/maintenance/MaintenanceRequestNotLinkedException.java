package ca.ulaval.trotti_ul.domain.maintenance;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class MaintenanceRequestNotLinkedException extends BusinessException {
    public MaintenanceRequestNotLinkedException() {
        super("NO_ACTIVE_MAINTENANCE_FOR_REQUEST", "No active maintenance linked to this request");
    }
}
