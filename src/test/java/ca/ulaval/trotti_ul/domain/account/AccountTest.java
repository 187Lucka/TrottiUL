package ca.ulaval.trotti_ul.domain.account;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import ca.ulaval.trotti_ul.domain.common.ValidationException;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @Test
    void shouldCreateUserAccountWithValidParameters() {
        // Given
        String idul = "test123";
        String name = "Test User";
        String email = "test@example.com";
        PasswordHash passwordHash = new PasswordHash("hashedpassword");
        DateOfBirth dateOfBirth = new DateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        AccountGender gender = new AccountGender(AccountGender.Gender.MALE);
        Instant now = Instant.now();

        // When
        Account account = Account.createUser(idul, name, email, passwordHash, dateOfBirth, gender, now);

        // Then
        assertThat(account).isNotNull();
        assertThat(account.idul()).isEqualTo(idul);
        assertThat(account.name().value()).isEqualTo(name);
        assertThat(account.email().value()).isEqualTo(email);
        assertThat(account.passwordHash()).isEqualTo(passwordHash);
        assertThat(account.dateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(account.gender()).isEqualTo(gender);
        assertThat(account.role()).isEqualTo(AccountRole.USER);
        assertThat(account.createdAt()).isEqualTo(now);
        assertThat(account.isEmployee()).isFalse();
    }

    @Test
    void shouldCreateEmployeeAccountWithValidParameters() {
        // Given
        String idul = "emp123";
        String name = "Employee User";
        String email = "employee@example.com";
        PasswordHash passwordHash = new PasswordHash("hashedpassword");
        DateOfBirth dateOfBirth = new DateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        AccountGender gender = new AccountGender(AccountGender.Gender.FEMALE);
        Instant now = Instant.now();

        // When
        Account account = Account.createEmployee(idul, name, email, passwordHash, dateOfBirth, gender, now);

        // Then
        assertThat(account).isNotNull();
        assertThat(account.idul()).isEqualTo(idul);
        assertThat(account.name().value()).isEqualTo(name);
        assertThat(account.email().value()).isEqualTo(email);
        assertThat(account.passwordHash()).isEqualTo(passwordHash);
        assertThat(account.dateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(account.gender()).isEqualTo(gender);
        assertThat(account.role()).isEqualTo(AccountRole.EMPLOYEE);
        assertThat(account.createdAt()).isEqualTo(now);
        assertThat(account.isEmployee()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenIdulIsNull() {
        // Given
        String idul = null;
        String name = "Test User";
        String email = "test@example.com";
        PasswordHash passwordHash = new PasswordHash("hashedpassword");
        DateOfBirth dateOfBirth = new DateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        AccountGender gender = new AccountGender(AccountGender.Gender.MALE);
        Instant now = Instant.now();

        // When & Then
        assertThrows(ValidationException.class, () -> {
            Account.createUser(idul, name, email, passwordHash, dateOfBirth, gender, now);
        });
    }

    @Test
    void shouldThrowExceptionWhenIdulIsBlank() {
        // Given
        String idul = "   ";
        String name = "Test User";
        String email = "test@example.com";
        PasswordHash passwordHash = new PasswordHash("hashedpassword");
        DateOfBirth dateOfBirth = new DateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        AccountGender gender = new AccountGender(AccountGender.Gender.MALE);
        Instant now = Instant.now();

        // When & Then
        assertThrows(ca.ulaval.trotti_ul.domain.common.ValidationException.class, () -> {
            Account.createUser(idul, name, email, passwordHash, dateOfBirth, gender, now);
        });
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // Given
        String idul = "test123";
        String name = null;
        String email = "test@example.com";
        PasswordHash passwordHash = new PasswordHash("hashedpassword");
        DateOfBirth dateOfBirth = new DateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        AccountGender gender = new AccountGender(AccountGender.Gender.MALE);
        Instant now = Instant.now();

        // When & Then
        assertThrows(ValidationException.class, () -> {
            Account.createUser(idul, name, email, passwordHash, dateOfBirth, gender, now);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        String idul = "test123";
        String name = "Test User";
        String email = null;
        PasswordHash passwordHash = new PasswordHash("hashedpassword");
        DateOfBirth dateOfBirth = new DateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        AccountGender gender = new AccountGender(AccountGender.Gender.MALE);
        Instant now = Instant.now();

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            Account.createUser(idul, name, email, passwordHash, dateOfBirth, gender, now);
        });
    }

    @Test
    void shouldThrowExceptionWhenPasswordHashIsNull() {
        // Given
        String idul = "test123";
        String name = "Test User";
        String email = "test@example.com";
        PasswordHash passwordHash = null;
        DateOfBirth dateOfBirth = new DateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        AccountGender gender = new AccountGender(AccountGender.Gender.MALE);
        Instant now = Instant.now();

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            Account.createUser(idul, name, email, passwordHash, dateOfBirth, gender, now);
        });
    }

    @Test
    void shouldThrowExceptionWhenDateOfBirthIsNull() {
        // Given
        String idul = "test123";
        String name = "Test User";
        String email = "test@example.com";
        PasswordHash passwordHash = new PasswordHash("hashedpassword");
        DateOfBirth dateOfBirth = null;
        AccountGender gender = new AccountGender(AccountGender.Gender.MALE);
        Instant now = Instant.now();

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            Account.createUser(idul, name, email, passwordHash, dateOfBirth, gender, now);
        });
    }

    @Test
    void shouldThrowExceptionWhenGenderIsNull() {
        // Given
        String idul = "test123";
        String name = "Test User";
        String email = "test@example.com";
        PasswordHash passwordHash = new PasswordHash("hashedpassword");
        DateOfBirth dateOfBirth = new DateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        AccountGender gender = null;
        Instant now = Instant.now();

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            Account.createUser(idul, name, email, passwordHash, dateOfBirth, gender, now);
        });
    }

    @Test
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        // Given
        String idul = "test123";
        String name = "Test User";
        String email = "test@example.com";
        PasswordHash passwordHash = new PasswordHash("hashedpassword");
        DateOfBirth dateOfBirth = new DateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        AccountGender gender = new AccountGender(AccountGender.Gender.MALE);
        Instant now = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            Account.createUser(idul, name, email, passwordHash, dateOfBirth, gender, now);
        });
    }
}