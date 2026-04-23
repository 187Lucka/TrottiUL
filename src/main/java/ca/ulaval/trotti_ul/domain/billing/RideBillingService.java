package ca.ulaval.trotti_ul.domain.billing;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.pass.BillingMode;
import ca.ulaval.trotti_ul.domain.pass.Pass;

public interface RideBillingService {
    void chargeRideOverage(AccountId accountId, Pass pass, int amountInCents, String rideId);

    default boolean supportsMode(BillingMode mode) {
        return true;
    }
}
