package ca.ulaval.trotti_ul.domain.payment;

public interface PaymentGateway {

    PaymentResult processPayment(CreditCard creditCard, int amountInCents);
}
