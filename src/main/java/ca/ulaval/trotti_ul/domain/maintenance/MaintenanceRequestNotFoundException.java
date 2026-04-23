package ca.ulaval.trotti_ul.domain.maintenance;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class MaintenanceRequestNotFoundException extends BusinessException {
    public MaintenanceRequestNotFoundException() {
        super("MAINTENANCE_REQUEST_NOT_FOUND", "Maintenance request not found");
    }
}
