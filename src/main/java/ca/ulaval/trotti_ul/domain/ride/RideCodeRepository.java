package ca.ulaval.trotti_ul.domain.ride;

import java.util.Optional;

import ca.ulaval.trotti_ul.domain.account.AccountId;

public interface RideCodeRepository {
    void save(AccountId accountId, GenerateCode code);
    Optional<GenerateCode> findByAccountId(AccountId accountId);
    void delete(AccountId accountId);
}
