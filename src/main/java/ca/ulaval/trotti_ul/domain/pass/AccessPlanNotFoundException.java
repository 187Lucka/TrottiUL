package ca.ulaval.trotti_ul.domain.pass;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class AccessPlanNotFoundException extends BusinessException {
    public AccessPlanNotFoundException() {
        super("NO_ACCESS_PLAN", "No access plan found for this account");
    }
}
