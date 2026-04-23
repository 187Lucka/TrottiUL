package ca.ulaval.trotti_ul.api.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddCreditCardRequest(
        @NotBlank @Pattern(regexp = "\\d{13,19}", message = "cardNumber must be 13-19 digits") String cardNumber,
        @NotBlank @Pattern(regexp = "\\d{2}/\\d{2}", message = "expiry must be MM/YY") String expiry,
        @NotBlank @Size(min = 3, max = 4) String cvv
) {}
