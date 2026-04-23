package ca.ulaval.trotti_ul.domain.technician;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class TechnicianAlreadyExistsException extends BusinessException {
    public TechnicianAlreadyExistsException() {
        super("TECHNICIAN_ALREADY_EXISTS", "Technician already exists");
    }
}
