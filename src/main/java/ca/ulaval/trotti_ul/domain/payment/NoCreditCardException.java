package ca.ulaval.trotti_ul.domain.payment;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class NoCreditCardException extends BusinessException {

    public NoCreditCardException() {
        super("NO_CREDIT_CARD", "No credit card is associated with this account. Please add a credit card first.");
    }
}
