package ca.ulaval.trotti_ul.domain.technician;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountRole;
import ca.ulaval.trotti_ul.domain.account.AccountName;
import ca.ulaval.trotti_ul.domain.account.EmailAddress;
import ca.ulaval.trotti_ul.domain.account.PasswordHash;
import ca.ulaval.trotti_ul.domain.account.DateOfBirth;
import ca.ulaval.trotti_ul.domain.account.AccountGender;

class TechnicianTest {

    @Test
    void shouldCreateTechnicianFromEmployeeAccount() {
        // Given
        Account employeeAccount = Account.createEmployee(
            "emp123",
            "Employee User",
            "employee@example.com",
            new PasswordHash("hashedpassword"),
            new DateOfBirth(java.time.LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.MALE),
            Instant.now()
        );

        // When
        Technician technician = Technician.create(employeeAccount);

        // Then
        assertThat(technician).isNotNull();
        assertThat(technician.id()).isNotNull();
        assertThat(technician.accountId()).isEqualTo(employeeAccount.id());
        assertThat(technician.isActive()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenCreatingTechnicianFromUserAccount() {
        // Given
        Account userAccount = Account.createUser(
            "user123",
            "User",
            "user@example.com",
            new PasswordHash("hashedpassword"),
            new DateOfBirth(java.time.LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.FEMALE),
            Instant.now()
        );

        // When & Then
        assertThrows(TechnicianMustBeEmployeeException.class, () -> {
            Technician.create(userAccount);
        });
    }

    @Test
    void shouldThrowExceptionWhenAccountIsNull() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            Technician.create(null);
        });
    }

    @Test
    void shouldDisableTechnician() {
        // Given
        Account employeeAccount = Account.createEmployee(
            "emp123",
            "Employee User",
            "employee@example.com",
            new PasswordHash("hashedpassword"),
            new DateOfBirth(java.time.LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.MALE),
            Instant.now()
        );
        Technician technician = Technician.create(employeeAccount);

        // When
        technician.disable();

        // Then
        assertThat(technician.isActive()).isFalse();
    }

    @Test
    void shouldHaveCorrectTechnicianId() {
        // Given
        Account employeeAccount = Account.createEmployee(
            "emp123",
            "Employee User",
            "employee@example.com",
            new PasswordHash("hashedpassword"),
            new DateOfBirth(java.time.LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.MALE),
            Instant.now()
        );
        Technician technician = Technician.create(employeeAccount);

        // When
        TechnicianId id = technician.id();

        // Then
        assertThat(id).isNotNull();
        assertThat(id.value()).isNotNull();
    }

    @Test
    void shouldHaveCorrectAccountId() {
        // Given
        Account employeeAccount = Account.createEmployee(
            "emp123",
            "Employee User",
            "employee@example.com",
            new PasswordHash("hashedpassword"),
            new DateOfBirth(java.time.LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.MALE),
            Instant.now()
        );
        Technician technician = Technician.create(employeeAccount);

        // When
        AccountId accountId = technician.accountId();

        // Then
        assertThat(accountId).isEqualTo(employeeAccount.id());
    }
}