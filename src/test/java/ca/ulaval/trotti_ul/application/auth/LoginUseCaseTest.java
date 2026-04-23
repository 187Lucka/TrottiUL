package ca.ulaval.trotti_ul.application.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.account.AccountRole;
import ca.ulaval.trotti_ul.domain.account.AccountName;
import ca.ulaval.trotti_ul.domain.account.EmailAddress;
import ca.ulaval.trotti_ul.domain.account.PasswordHash;
import ca.ulaval.trotti_ul.domain.account.DateOfBirth;
import ca.ulaval.trotti_ul.domain.account.AccountGender;
import ca.ulaval.trotti_ul.domain.auth.InvalidCredentialsException;
import ca.ulaval.trotti_ul.domain.security.PasswordHasher;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianId;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;
import ca.ulaval.trotti_ul.domain.technician.TechnicianStatus;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenService tokenService;

    @Mock
    private Clock clock;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    void shouldLoginUserSuccessfully() {
        // Given
        String email = "user@example.com";
        String password = "password123";
        String hashedPassword = "hashedpassword";
        
        AccountId accountId = AccountId.newId();
        Account userAccount = createUserAccount(accountId, email, hashedPassword);
        
        LoginCommand command = new LoginCommand(email, password);
        
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));
        when(passwordHasher.matches(password, new PasswordHash(hashedPassword))).thenReturn(true);
        when(clock.instant()).thenReturn(Instant.now());
        
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(3600); // 1 hour
        JwtClaims expectedClaims = new JwtClaims(
            accountId.toString(),
            Set.of("USER"),
            now,
            exp,
            null
        );
        
        when(tokenService.encode(any(JwtClaims.class))).thenReturn("mocked-token");

        // When
        AuthToken result = loginUseCase.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("mocked-token");
        
        verify(accountRepository, times(1)).findByEmail(email);
        verify(passwordHasher, times(1)).matches(password, new PasswordHash(hashedPassword));
        verify(technicianRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void shouldLoginEmployeeSuccessfully() {
        // Given
        String email = "employee@example.com";
        String password = "password123";
        String hashedPassword = "hashedpassword";
        
        AccountId accountId = AccountId.newId();
        Account employeeAccount = createEmployeeAccount(accountId, email, hashedPassword);
        
        LoginCommand command = new LoginCommand(email, password);
        
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(employeeAccount));
        when(passwordHasher.matches(password, new PasswordHash(hashedPassword))).thenReturn(true);
        when(clock.instant()).thenReturn(Instant.now());
        when(technicianRepository.findByAccountId(accountId)).thenReturn(Optional.empty());
        when(tokenService.encode(any(JwtClaims.class))).thenReturn("mocked-token");

        // When
        AuthToken result = loginUseCase.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("mocked-token");
        
        verify(accountRepository, times(1)).findByEmail(email);
        verify(passwordHasher, times(1)).matches(password, new PasswordHash(hashedPassword));
        verify(technicianRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void shouldLoginTechnicianSuccessfully() {
        // Given
        String email = "technician@example.com";
        String password = "password123";
        String hashedPassword = "hashedpassword";
        
        AccountId accountId = AccountId.newId();
        Account employeeAccount = createEmployeeAccount(accountId, email, hashedPassword);
        Technician technician = createActiveTechnician(accountId);
        
        LoginCommand command = new LoginCommand(email, password);
        
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(employeeAccount));
        when(passwordHasher.matches(password, new PasswordHash(hashedPassword))).thenReturn(true);
        when(clock.instant()).thenReturn(Instant.now());
        when(technicianRepository.findByAccountId(accountId)).thenReturn(Optional.of(technician));
        when(tokenService.encode(any(JwtClaims.class))).thenReturn("mocked-token");

        // When
        AuthToken result = loginUseCase.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("mocked-token");
        
        verify(accountRepository, times(1)).findByEmail(email);
        verify(passwordHasher, times(1)).matches(password, new PasswordHash(hashedPassword));
        verify(technicianRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        // Given
        String email = "nonexistent@example.com";
        String password = "password123";
        
        LoginCommand command = new LoginCommand(email, password);
        
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            loginUseCase.handle(command);
        });
        
        verify(accountRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(passwordHasher);
        verifyNoMoreInteractions(technicianRepository);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        // Given
        String email = "user@example.com";
        String password = "wrongpassword";
        String hashedPassword = "hashedpassword";
        
        AccountId accountId = AccountId.newId();
        Account userAccount = createUserAccount(accountId, email, hashedPassword);
        
        LoginCommand command = new LoginCommand(email, password);
        
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(userAccount));
        when(passwordHasher.matches(password, new PasswordHash(hashedPassword))).thenReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            loginUseCase.handle(command);
        });
        
        verify(accountRepository, times(1)).findByEmail(email);
        verify(passwordHasher, times(1)).matches(password, new PasswordHash(hashedPassword));
        verifyNoMoreInteractions(technicianRepository);
    }

    @Test
    void shouldNotIncludeTechnicianRoleWhenTechnicianIsDisabled() {
        // Given
        String email = "technician@example.com";
        String password = "password123";
        String hashedPassword = "hashedpassword";
        
        AccountId accountId = AccountId.newId();
        Account employeeAccount = createEmployeeAccount(accountId, email, hashedPassword);
        Technician disabledTechnician = createDisabledTechnician(accountId);
        
        LoginCommand command = new LoginCommand(email, password);
        
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(employeeAccount));
        when(passwordHasher.matches(password, new PasswordHash(hashedPassword))).thenReturn(true);
        when(tokenService.encode(any())).thenReturn("mocked-token");
        when(clock.instant()).thenReturn(Instant.now());

        // When
        AuthToken result = loginUseCase.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("mocked-token");
        
        // Verify that the technician role was not included
        verify(tokenService).encode(argThat(claims -> 
            !claims.roles().contains("TECHNICIAN")
        ));
    }

    private Account createUserAccount(AccountId accountId, String email, String hashedPassword) {
        return new Account(
            accountId,
            "user123",
            AccountName.of("User"),
            EmailAddress.of(email),
            new PasswordHash(hashedPassword),
            new DateOfBirth(java.time.LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.MALE),
            AccountRole.USER,
            Instant.now()
        );
    }

    private Account createEmployeeAccount(AccountId accountId, String email, String hashedPassword) {
        return new Account(
            accountId,
            "emp123",
            AccountName.of("Employee"),
            EmailAddress.of(email),
            new PasswordHash(hashedPassword),
            new DateOfBirth(java.time.LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.FEMALE),
            AccountRole.EMPLOYEE,
            Instant.now()
        );
    }

    private Technician createActiveTechnician(AccountId accountId) {
        // Use reflection to create technician since constructor is private
        try {
            java.lang.reflect.Constructor<Technician> constructor = Technician.class.getDeclaredConstructor(
                TechnicianId.class, AccountId.class, TechnicianStatus.class
            );
            constructor.setAccessible(true);
            return constructor.newInstance(TechnicianId.newId(), accountId, TechnicianStatus.ACTIVE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create technician", e);
        }
    }

    private Technician createDisabledTechnician(AccountId accountId) {
        // Use reflection to create technician since constructor is private
        try {
            java.lang.reflect.Constructor<Technician> constructor = Technician.class.getDeclaredConstructor(
                TechnicianId.class, AccountId.class, TechnicianStatus.class
            );
            constructor.setAccessible(true);
            return constructor.newInstance(TechnicianId.newId(), accountId, TechnicianStatus.DISABLED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create technician", e);
        }
    }
}