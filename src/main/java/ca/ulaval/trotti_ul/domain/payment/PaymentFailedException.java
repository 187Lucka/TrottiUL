package ca.ulaval.trotti_ul.domain.payment;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class PaymentFailedException extends BusinessException {

    public PaymentFailedException(String message) {
        super("PAYMENT_FAILED", message);
    }
}
