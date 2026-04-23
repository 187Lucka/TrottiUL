package ca.ulaval.trotti_ul.domain.technician;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class TechnicianMustBeUlEmployeeException extends BusinessException {
    public TechnicianMustBeUlEmployeeException() {
        super("TECHNICIAN_MUST_BE_UL_EMPLOYEE", "Technician must be a UL employee");
    }
}
