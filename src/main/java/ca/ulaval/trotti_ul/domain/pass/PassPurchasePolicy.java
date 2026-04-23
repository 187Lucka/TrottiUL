package ca.ulaval.trotti_ul.domain.pass;

import java.time.Instant;
import java.time.LocalDate;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountRole;
import ca.ulaval.trotti_ul.domain.semester.InvalidSemesterException;
import ca.ulaval.trotti_ul.domain.semester.Semester;

public class PassPurchasePolicy {

    public Pass createPass(Account account,
                           Semester semester,
                           DailyTripDuration duration,
                           BillingMode billingMode,
                           LocalDate today,
                           Instant now) {
        if (account.role() == AccountRole.EMPLOYEE) {
            throw new EmployeeCannotPurchasePassException();
        }
        if (!semester.isPurchasable(today)) {
            throw InvalidSemesterException.notPurchasable(semester.code());
        }
        return Pass.create(account.id(), semester.code(), duration, billingMode, now);
    }
}
