package ca.ulaval.trotti_ul.domain.ride;

import java.util.List;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.account.AccountId;

public interface RideRepository {
    void saveActive(Ride ride);
    Optional<Ride> findActiveByAccountId(AccountId accountId);
    void completeRide(Ride ride);
    List<Ride> findByAccountId(AccountId accountId);
}
