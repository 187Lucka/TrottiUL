package ca.ulaval.trotti_ul.domain.maintenance;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class MaintenanceRequestNotPendingException extends BusinessException {
    public MaintenanceRequestNotPendingException() {
        super("MAINTENANCE_REQUEST_NOT_PENDING", "Maintenance request is not pending");
    }
}
