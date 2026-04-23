package ca.ulaval.trotti_ul.domain.pass;

import ca.ulaval.trotti_ul.domain.common.BusinessException;
import ca.ulaval.trotti_ul.domain.semester.SemesterCode;

public class PassAlreadyExistsException extends BusinessException {

    public PassAlreadyExistsException(SemesterCode semesterCode) {
        super("PASS_ALREADY_EXISTS", "A pass already exists for semester " + semesterCode.value());
    }
}
