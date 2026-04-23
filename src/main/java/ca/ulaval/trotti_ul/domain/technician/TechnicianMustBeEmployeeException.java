package ca.ulaval.trotti_ul.domain.technician;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class TechnicianMustBeEmployeeException extends BusinessException {
    public TechnicianMustBeEmployeeException() {
        super("TECHNICIAN_MUST_BE_EMPLOYEE");
    }
}
