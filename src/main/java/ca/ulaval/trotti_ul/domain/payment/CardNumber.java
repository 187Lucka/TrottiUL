package ca.ulaval.trotti_ul.domain.payment;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public record CardNumber(String maskedValue, String lastFourDigits) {

    public CardNumber {
        if (maskedValue == null || maskedValue.isBlank()) {
            throw new ValidationException("Card number must not be empty");
        }
        if (lastFourDigits == null || lastFourDigits.length() != 4) {
            throw new ValidationException("Last four digits must be exactly 4 characters");
        }
    }

    public static CardNumber fromFullNumber(String fullNumber) {
        if (fullNumber == null || fullNumber.isBlank()) {
            throw new ValidationException("Card number must not be empty");
        }

        String digitsOnly = fullNumber.replaceAll("[^0-9]", "");

        if (digitsOnly.length() < 13 || digitsOnly.length() > 19) {
            throw new ValidationException("Card number must be between 13 and 19 digits");
        }

        String lastFour = digitsOnly.substring(digitsOnly.length() - 4);
        String masked = "**** **** **** " + lastFour;

        return new CardNumber(masked, lastFour);
    }

    @Override
    public String toString() {
        return maskedValue;
    }
}
