package ca.ulaval.trotti_ul.infrastructure.payment;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.payment.CreditCard;
import ca.ulaval.trotti_ul.domain.payment.CreditCardRepository;

public class InMemoryCreditCardRepository implements CreditCardRepository {

    private final Map<AccountId, CreditCard> byAccountId = new ConcurrentHashMap<>();

    @Override
    public void save(CreditCard creditCard) {
        byAccountId.put(creditCard.accountId(), creditCard);
    }

    @Override
    public Optional<CreditCard> findByAccountId(AccountId accountId) {
        return Optional.ofNullable(byAccountId.get(accountId));
    }

    @Override
    public void deleteByAccountId(AccountId accountId) {
        byAccountId.remove(accountId);
    }
}
