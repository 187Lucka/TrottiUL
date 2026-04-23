package ca.ulaval.trotti_ul.domain.maintenance;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class MaintenanceAlreadyCompletedException extends BusinessException {
    public MaintenanceAlreadyCompletedException() {
        super("MAINTENANCE_ALREADY_COMPLETED", "Maintenance is already completed");
    }
}
