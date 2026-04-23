package ca.ulaval.trotti_ul.application.payment;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.payment.CreditCard;
import ca.ulaval.trotti_ul.domain.payment.CreditCardRepository;

public class AddCreditCardUseCase {

    private final CreditCardRepository creditCardRepository;

    public AddCreditCardUseCase(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public CreditCard handle(AddCreditCardCommand command) {
        AccountId accountId = AccountId.fromString(command.accountId());

        CreditCard creditCard = CreditCard.create(
                accountId,
                command.cardNumber(),
                command.expiry(),
                command.cvv()
        );

        creditCardRepository.deleteByAccountId(accountId);
        creditCardRepository.save(creditCard);

        return creditCard;
    }
}
