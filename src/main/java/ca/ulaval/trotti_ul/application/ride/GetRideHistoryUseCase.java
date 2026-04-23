package ca.ulaval.trotti_ul.application.ride;

import java.util.List;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.ride.Ride;
import ca.ulaval.trotti_ul.domain.ride.RideRepository;

public class GetRideHistoryUseCase {

    private final RideRepository rideRepository;

    public GetRideHistoryUseCase(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public List<Ride> handle(String accountIdString) {
        AccountId accountId = AccountId.fromString(accountIdString);
        return rideRepository.findByAccountId(accountId);
    }
}
