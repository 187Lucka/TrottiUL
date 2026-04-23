package ca.ulaval.trotti_ul.domain.maintenance;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class NotATechnicianException extends BusinessException {

    public NotATechnicianException() {
        super("NOT_A_TECHNICIAN", "Only technicians can perform this operation");
    }
}
