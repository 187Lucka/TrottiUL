package ca.ulaval.trotti_ul.application.payment;

public record AddCreditCardCommand(
        String accountId,
        String cardNumber,
        String expiry,
        String cvv
) {}
