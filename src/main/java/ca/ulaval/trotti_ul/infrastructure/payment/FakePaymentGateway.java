package ca.ulaval.trotti_ul.infrastructure.payment;

import java.util.UUID;

import ca.ulaval.trotti_ul.domain.payment.CreditCard;
import ca.ulaval.trotti_ul.domain.payment.PaymentGateway;
import ca.ulaval.trotti_ul.domain.payment.PaymentResult;

public class FakePaymentGateway implements PaymentGateway {

    private static final String DECLINED_CARD_SUFFIX = "0000";

    @Override
    public PaymentResult processPayment(CreditCard creditCard, int amountInCents) {
        if (creditCard.cardNumber().lastFourDigits().equals(DECLINED_CARD_SUFFIX)) {
            return PaymentResult.failed("Card declined by issuer");
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return PaymentResult.successful(transactionId);
    }
}
