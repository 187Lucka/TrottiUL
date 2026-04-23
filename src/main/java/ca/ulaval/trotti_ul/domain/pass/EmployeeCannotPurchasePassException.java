package ca.ulaval.trotti_ul.domain.pass;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class EmployeeCannotPurchasePassException extends BusinessException {
    public EmployeeCannotPurchasePassException() {
        super("EMPLOYEE_CANNOT_PURCHASE_PASS", "Employees cannot purchase passes");
    }
}
