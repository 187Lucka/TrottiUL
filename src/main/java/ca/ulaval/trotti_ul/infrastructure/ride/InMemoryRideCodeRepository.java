package ca.ulaval.trotti_ul.infrastructure.ride;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.ride.GenerateCode;
import ca.ulaval.trotti_ul.domain.ride.RideCodeRepository;

public class InMemoryRideCodeRepository implements RideCodeRepository {

    private final Map<AccountId, GenerateCode> codes = new ConcurrentHashMap<>();

    @Override
    public void save(AccountId accountId, GenerateCode code) {
        codes.put(accountId, code);
    }

    @Override
    public Optional<GenerateCode> findByAccountId(AccountId accountId) {
        return Optional.ofNullable(codes.get(accountId));
    }

    @Override
    public void delete(AccountId accountId) {
        codes.remove(accountId);
    }
}
