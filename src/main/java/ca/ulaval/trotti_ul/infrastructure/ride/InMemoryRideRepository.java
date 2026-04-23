package ca.ulaval.trotti_ul.infrastructure.ride;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.ride.Ride;
import ca.ulaval.trotti_ul.domain.ride.RideRepository;

public class InMemoryRideRepository implements RideRepository {

    private final Map<AccountId, Ride> active = new ConcurrentHashMap<>();
    private final List<Ride> history = new ArrayList<>();

    @Override
    public void saveActive(Ride ride) {
        active.put(ride.accountId(), ride);
    }

    @Override
    public Optional<Ride> findActiveByAccountId(AccountId accountId) {
        return Optional.ofNullable(active.get(accountId));
    }

    @Override
    public void completeRide(Ride ride) {
        active.remove(ride.accountId());
        history.add(ride);
    }

    @Override
    public List<Ride> findByAccountId(AccountId accountId) {
        return history.stream()
                .filter(r -> r.accountId().equals(accountId))
                .collect(Collectors.toList());
    }
}
