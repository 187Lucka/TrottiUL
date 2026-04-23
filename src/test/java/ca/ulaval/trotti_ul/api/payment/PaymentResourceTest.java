package ca.ulaval.trotti_ul.api.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import ca.ulaval.trotti_ul.api.payment.dto.AddCreditCardRequest;
import ca.ulaval.trotti_ul.api.payment.dto.CreditCardResponse;
import ca.ulaval.trotti_ul.application.payment.AddCreditCardCommand;
import ca.ulaval.trotti_ul.application.payment.AddCreditCardUseCase;
import ca.ulaval.trotti_ul.domain.payment.CreditCard;
import ca.ulaval.trotti_ul.domain.payment.CreditCardId;
import ca.ulaval.trotti_ul.domain.payment.CardNumber;
import ca.ulaval.trotti_ul.domain.payment.CardExpiry;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@ExtendWith(MockitoExtension.class)
class PaymentResourceTest {

    @Mock
    private AddCreditCardUseCase addCreditCardUseCase;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private PaymentResource paymentResource;

    @Test
    void addCreditCard_shouldReturnCreatedCard_whenRequestIsValid() {
        // Given
        AddCreditCardRequest request = new AddCreditCardRequest(
            "4111111111111111",
            "12/25",
            "123"
        );

        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        CreditCardId cardId = CreditCardId.newId();
        CreditCard creditCard = new CreditCard(
            cardId,
            AccountId.fromString(accountId),
            CardNumber.fromFullNumber("4111111111111111"),
            CardExpiry.of("12/25"),
            "123"
        );

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(addCreditCardUseCase.handle(any(AddCreditCardCommand.class))).thenReturn(creditCard);

        // When
        Response response = paymentResource.addCreditCard(securityContext, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(201);

        CreditCardResponse result = (CreditCardResponse) response.getEntity();
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(cardId.toString());
        assertThat(result.maskedCardNumber()).isEqualTo("**** **** **** 1111");
        assertThat(result.expiry()).isEqualTo("12/25");

        verify(addCreditCardUseCase, times(1)).handle(any(AddCreditCardCommand.class));
    }

    @Test
    void addCreditCard_shouldThrowException_whenCardNumberIsInvalid() {
        // Given
        AddCreditCardRequest request = new AddCreditCardRequest(
            "123", // Invalid card number (too short)
            "12/25",
            "123"
        );

        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(addCreditCardUseCase.handle(any(AddCreditCardCommand.class)))
            .thenThrow(new IllegalArgumentException("Invalid card number"));

        // When/Then
        assertThatThrownBy(() -> paymentResource.addCreditCard(securityContext, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid card number");
    }

    @Test
    void addCreditCard_shouldThrowException_whenExpiryIsInvalid() {
        // Given
        AddCreditCardRequest request = new AddCreditCardRequest(
            "4111111111111111",
            "13/25", // Invalid month
            "123"
        );

        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(addCreditCardUseCase.handle(any(AddCreditCardCommand.class)))
            .thenThrow(new IllegalArgumentException("Invalid expiry date"));

        // When/Then
        assertThatThrownBy(() -> paymentResource.addCreditCard(securityContext, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid expiry date");
    }

    @Test
    void addCreditCard_shouldThrowException_whenCVVIsInvalid() {
        // Given
        AddCreditCardRequest request = new AddCreditCardRequest(
            "4111111111111111",
            "12/25",
            "12" // Invalid CVV (too short)
        );

        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(addCreditCardUseCase.handle(any(AddCreditCardCommand.class)))
            .thenThrow(new IllegalArgumentException("Invalid CVV"));

        // When/Then
        assertThatThrownBy(() -> paymentResource.addCreditCard(securityContext, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid CVV");
    }

    @Test
    void addCreditCard_shouldThrowException_whenUseCaseFails() {
        // Given
        AddCreditCardRequest request = new AddCreditCardRequest(
            "4111111111111111",
            "12/25",
            "123"
        );

        String accountId = "account-123";
        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(addCreditCardUseCase.handle(any(AddCreditCardCommand.class)))
            .thenThrow(new RuntimeException("Payment gateway error"));

        // When/Then
        assertThatThrownBy(() -> paymentResource.addCreditCard(securityContext, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Payment gateway error");
    }
}