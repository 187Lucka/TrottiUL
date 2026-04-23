package ca.ulaval.trotti_ul.api.payment.dto;

import ca.ulaval.trotti_ul.domain.payment.CreditCard;

public record CreditCardResponse(
        String id,
        String maskedCardNumber,
        String expiry
) {
    public static CreditCardResponse from(CreditCard card) {
        return new CreditCardResponse(
                card.id().toString(),
                card.cardNumber().maskedValue(),
                card.expiry().toString()
        );
    }
}
