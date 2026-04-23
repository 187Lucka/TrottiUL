package ca.ulaval.trotti_ul.domain.payment;

import java.time.YearMonth;
import java.util.Objects;

import ca.ulaval.trotti_ul.domain.account.AccountId;

public class CreditCard {

    private final CreditCardId id;
    private final AccountId accountId;
    private final CardNumber cardNumber;
    private final CardExpiry expiry;
    private final String cvv;

    public CreditCard(CreditCardId id,
                      AccountId accountId,
                      CardNumber cardNumber,
                      CardExpiry expiry,
                      String cvv) {
        this.id = Objects.requireNonNull(id);
        this.accountId = Objects.requireNonNull(accountId);
        this.cardNumber = Objects.requireNonNull(cardNumber);
        this.expiry = Objects.requireNonNull(expiry);
        this.cvv = Objects.requireNonNull(cvv);
    }

    public static CreditCard create(AccountId accountId,
                                    String fullCardNumber,
                                    String expiryString,
                                    String cvv) {
        return new CreditCard(
                CreditCardId.newId(),
                accountId,
                CardNumber.fromFullNumber(fullCardNumber),
                CardExpiry.of(expiryString),
                cvv
        );
    }

    public CreditCardId id() {
        return id;
    }

    public AccountId accountId() {
        return accountId;
    }

    public CardNumber cardNumber() {
        return cardNumber;
    }

    public CardExpiry expiry() {
        return expiry;
    }

    public String cvv() {
        return cvv;
    }

    public boolean isExpired(YearMonth currentMonth) {
        return expiry.isExpired(currentMonth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditCard that = (CreditCard) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
