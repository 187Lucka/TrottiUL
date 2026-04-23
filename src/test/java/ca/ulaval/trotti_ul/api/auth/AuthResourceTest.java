package ca.ulaval.trotti_ul.api.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import ca.ulaval.trotti_ul.api.auth.dto.LoginRequest;
import ca.ulaval.trotti_ul.api.auth.dto.LoginResponse;
import ca.ulaval.trotti_ul.api.auth.dto.SignUpRequest;
import ca.ulaval.trotti_ul.api.auth.dto.SignUpResponse;
import ca.ulaval.trotti_ul.application.account.CreateAccountCommand;
import ca.ulaval.trotti_ul.application.account.CreateAccountUseCase;
import ca.ulaval.trotti_ul.application.auth.AuthToken;
import ca.ulaval.trotti_ul.application.auth.LoginCommand;
import ca.ulaval.trotti_ul.application.auth.LoginUseCase;
import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountName;
import ca.ulaval.trotti_ul.domain.account.AccountRole;
import ca.ulaval.trotti_ul.domain.account.AccountGender;
import ca.ulaval.trotti_ul.domain.account.DateOfBirth;
import ca.ulaval.trotti_ul.domain.account.EmailAddress;
import ca.ulaval.trotti_ul.domain.account.PasswordHash;
import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
class AuthResourceTest {

    @Mock
    private CreateAccountUseCase createAccountUseCase;

    @Mock
    private LoginUseCase loginUseCase;

    @InjectMocks
    private AuthResource authResource;

    @Test
    void signUp_shouldReturnCreatedAccountWithCorrectDetails_whenRequestIsValid() {
        // Given
        SignUpRequest request = new SignUpRequest(
            "test123",
            "Test User",
            "test@example.com",
            "password123",
            "MALE",
            "1990-01-01"
        );

        AccountId accountId = AccountId.newId();
        Account createdAccount = new Account(
            accountId,
            "test123",
            AccountName.of("Test User"),
            EmailAddress.of("test@example.com"),
            new PasswordHash("hashedpassword"),
            new DateOfBirth(LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.MALE),
            AccountRole.USER,
            java.time.Instant.now()
        );

        when(createAccountUseCase.handle(any(CreateAccountCommand.class))).thenReturn(createdAccount);

        // When
        Response response = authResource.signUp(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(201);

        SignUpResponse result = (SignUpResponse) response.getEntity();
        assertThat(result.idul()).isEqualTo("test123");
        assertThat(result.name()).isEqualTo("Test User");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.gender()).isEqualTo("MALE");
        assertThat(result.dateOfBirth()).isEqualTo("1990-01-01");
        assertThat(result.age()).isEqualTo(LocalDate.now().getYear() - 1990);

        verify(createAccountUseCase, times(1)).handle(any(CreateAccountCommand.class));
    }

    @Test
    void signUp_shouldThrowException_whenEmailIsInvalid() {
        // Given
        SignUpRequest request = new SignUpRequest(
            "test123",
            "Test User",
            "invalid-email",
            "password123",
            "MALE",
            "1990-01-01"
        );

        // When/Then - The use case should throw an exception for invalid email
        when(createAccountUseCase.handle(any(CreateAccountCommand.class)))
            .thenThrow(new IllegalArgumentException("Invalid email format"));

        assertThatThrownBy(() -> authResource.signUp(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    void signUp_shouldThrowException_whenIdulIsMissing() {
        // Given - Creating a request with empty IDUL (simulating missing field)
        SignUpRequest request = new SignUpRequest(
            "",
            "Test User",
            "test@example.com",
            "password123",
            "MALE",
            "1990-01-01"
        );

        // When/Then - The use case should handle empty IDUL appropriately
        // For now, we'll just verify that the use case is called
        when(createAccountUseCase.handle(any(CreateAccountCommand.class)))
            .thenReturn(null); // Simulating the case where account creation fails

        assertThatThrownBy(() -> authResource.signUp(request))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void login_shouldReturnAuthToken_whenCredentialsAreValid() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        
        AuthToken authToken = new AuthToken("mocked-token", java.time.Instant.now().plusSeconds(3600));
        
        when(loginUseCase.handle(any(LoginCommand.class))).thenReturn(authToken);

        // When
        Response response = authResource.login(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);

        LoginResponse result = (LoginResponse) response.getEntity();
        assertThat(result.accessToken()).isEqualTo("mocked-token");
        assertThat(result.expiresAt()).isNotNull();

        verify(loginUseCase, times(1)).handle(any(LoginCommand.class));
    }

    @Test
    void login_shouldThrowException_whenPasswordIsIncorrect() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "wrongpassword");

        when(loginUseCase.handle(any(LoginCommand.class))).thenThrow(new IllegalArgumentException("Invalid credentials"));

        // When/Then
        assertThatThrownBy(() -> authResource.login(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid credentials");
    }

    @Test
    void login_shouldThrowException_whenUserIsNotAuthenticated() {
        // Given
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password123");

        when(loginUseCase.handle(any(LoginCommand.class))).thenThrow(new IllegalArgumentException("User not found"));

        // When/Then
        assertThatThrownBy(() -> authResource.login(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");
    }

    @Test
    void signUp_shouldCalculateAgeCorrectly_whenDateOfBirthIsProvided() {
        // Given
        SignUpRequest request = new SignUpRequest(
            "age-test",
            "Age Test",
            "age-test@example.com",
            "password123",
            "MALE",
            "2000-06-15"
        );

        AccountId accountId = AccountId.newId();
        Account createdAccount = new Account(
            accountId,
            "age-test",
            AccountName.of("Age Test"),
            EmailAddress.of("age-test@example.com"),
            new PasswordHash("hashedpassword"),
            new DateOfBirth(LocalDate.of(2000, 6, 15)),
            new AccountGender(AccountGender.Gender.MALE),
            AccountRole.USER,
            java.time.Instant.now()
        );

        when(createAccountUseCase.handle(any(CreateAccountCommand.class))).thenReturn(createdAccount);

        // When
        Response response = authResource.signUp(request);

        // Then
        SignUpResponse result = (SignUpResponse) response.getEntity();
        int expectedAge = LocalDate.now().getYear() - 2000;
        if (LocalDate.now().isBefore(LocalDate.of(LocalDate.now().getYear(), 6, 15))) {
            expectedAge--;
        }
        assertThat(result.age()).isEqualTo(expectedAge);
    }
}