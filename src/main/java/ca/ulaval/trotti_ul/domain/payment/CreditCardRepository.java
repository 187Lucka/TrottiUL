package ca.ulaval.trotti_ul.domain.payment;

import java.util.Optional;

import ca.ulaval.trotti_ul.domain.account.AccountId;

public interface CreditCardRepository {

    void save(CreditCard creditCard);

    Optional<CreditCard> findByAccountId(AccountId accountId);

    void deleteByAccountId(AccountId accountId);
}
