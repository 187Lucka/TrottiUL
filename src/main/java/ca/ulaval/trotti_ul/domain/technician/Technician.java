package ca.ulaval.trotti_ul.domain.technician;

import java.util.Objects;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountRole;

public class Technician {

    private final TechnicianId id;
    private final AccountId accountId;
    private TechnicianStatus status;

    private Technician(TechnicianId id,
                       AccountId accountId,
                       TechnicianStatus status) {
        this.id = Objects.requireNonNull(id);
        this.accountId = Objects.requireNonNull(accountId);
        this.status = Objects.requireNonNull(status);
    }

    public static Technician create(Account account) {
        if (account.role() != AccountRole.EMPLOYEE) {
            throw new TechnicianMustBeEmployeeException();
        }
        return new Technician(
                TechnicianId.newId(),
                account.id(),
                TechnicianStatus.ACTIVE
        );
    }

    public TechnicianId id() {
        return id;
    }

    public AccountId accountId() {
        return accountId;
    }

    public boolean isActive() {
        return status == TechnicianStatus.ACTIVE;
    }

    public void disable() {
        this.status = TechnicianStatus.DISABLED;
    }
}
