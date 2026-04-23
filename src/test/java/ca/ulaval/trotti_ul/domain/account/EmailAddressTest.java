package ca.ulaval.trotti_ul.domain.account;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class EmailAddressTest {

    @Test
    void shouldCreateEmailAddressWithValidEmail() {
        // Given
        String validEmail = "test@example.com";

        // When
        EmailAddress emailAddress = EmailAddress.of(validEmail);

        // Then
        assertThat(emailAddress).isNotNull();
        assertThat(emailAddress.value()).isEqualTo(validEmail);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        String nullEmail = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            EmailAddress.of(nullEmail);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        String invalidEmail = "invalid-email";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            EmailAddress.of(invalidEmail);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailHasNoAtSymbol() {
        // Given
        String invalidEmail = "testexample.com";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            EmailAddress.of(invalidEmail);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailHasNoDomain() {
        // Given
        String invalidEmail = "test@";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            EmailAddress.of(invalidEmail);
        });
    }

    @Test
    void shouldAcceptEmailWithSubdomain() {
        // Given
        String validEmail = "test@sub.example.com";

        // When
        EmailAddress emailAddress = EmailAddress.of(validEmail);

        // Then
        assertThat(emailAddress).isNotNull();
        assertThat(emailAddress.value()).isEqualTo(validEmail);
    }

    @Test
    void shouldHaveCorrectToString() {
        // Given
        String validEmail = "test@example.com";
        EmailAddress emailAddress = EmailAddress.of(validEmail);

        // When
        String result = emailAddress.toString();

        // Then
        assertThat(result).contains(validEmail);
    }
}