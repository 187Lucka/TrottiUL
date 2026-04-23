package ca.ulaval.trotti_ul.domain.semester;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class InvalidSemesterException extends BusinessException {

    public InvalidSemesterException(SemesterCode code) {
        super("INVALID_SEMESTER", "Semester not found: " + code.value());
    }

    public static InvalidSemesterException notPurchasable(SemesterCode code) {
        return new InvalidSemesterException("SEMESTER_NOT_PURCHASABLE",
                "Semester " + code.value() + " is no longer available for purchase");
    }

    private InvalidSemesterException(String code, String message) {
        super(code, message);
    }
}
