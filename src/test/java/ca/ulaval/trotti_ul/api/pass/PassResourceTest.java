package ca.ulaval.trotti_ul.api.pass;

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

import ca.ulaval.trotti_ul.api.pass.dto.PurchasePassRequest;
import ca.ulaval.trotti_ul.api.pass.dto.PurchasePassResponse;
import ca.ulaval.trotti_ul.api.pass.dto.PassResponse;
import ca.ulaval.trotti_ul.application.pass.PurchasePassCommand;
import ca.ulaval.trotti_ul.application.pass.PurchasePassResult;
import ca.ulaval.trotti_ul.application.pass.PurchasePassUseCase;
import ca.ulaval.trotti_ul.application.pass.GetAccountPassesUseCase;
import ca.ulaval.trotti_ul.application.pass.GetValidPassUseCase;
import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountName;
import ca.ulaval.trotti_ul.domain.account.AccountRole;
import ca.ulaval.trotti_ul.domain.account.EmailAddress;
import ca.ulaval.trotti_ul.domain.account.PasswordHash;
import ca.ulaval.trotti_ul.domain.account.DateOfBirth;
import ca.ulaval.trotti_ul.domain.account.AccountGender;
import ca.ulaval.trotti_ul.domain.pass.Pass;
import ca.ulaval.trotti_ul.domain.pass.PassId;
import ca.ulaval.trotti_ul.domain.pass.EffectivePass;
import ca.ulaval.trotti_ul.domain.pass.AccessPlanService;
import ca.ulaval.trotti_ul.domain.pass.DailyTripDuration;
import ca.ulaval.trotti_ul.domain.pass.BillingMode;
import ca.ulaval.trotti_ul.domain.semester.SemesterCode;
import ca.ulaval.trotti_ul.domain.billing.Invoice;
import ca.ulaval.trotti_ul.domain.billing.InvoiceId;
import ca.ulaval.trotti_ul.domain.billing.InvoiceStatus;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.account.AccountNotFoundException;
import ca.ulaval.trotti_ul.domain.pass.AccessPlanNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PassResourceTest {

    @Mock
    private PurchasePassUseCase purchasePassUseCase;

    @Mock
    private GetAccountPassesUseCase getAccountPassesUseCase;

    @Mock
    private GetValidPassUseCase getValidPassUseCase;

    @Mock
    private AccessPlanService accessPlanService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private PassResource passResource;

    @Test
    void purchasePass_shouldReturnCreatedPass_whenRequestIsValid() {
        // Given
        PurchasePassRequest request = new PurchasePassRequest(
            "A25", // Semester code
            30,      // Daily trip duration
            "MONTHLY" // Billing mode
        );

        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        PassId passId = PassId.newId();
        Pass pass = new Pass(
            passId,
            AccountId.fromString(accountId),
            SemesterCode.of("A25"),
            DailyTripDuration.of(30),
            BillingMode.MONTHLY,
            5000, // 50.00 in cents
            Instant.now()
        );

        Invoice invoice = Invoice.createForPass(
            AccountId.fromString(accountId),
            passId,
            SemesterCode.of("A25"),
            DailyTripDuration.of(30),
            "txn-123",
            Instant.now()
        );

        PurchasePassResult result = new PurchasePassResult(pass, invoice);

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(purchasePassUseCase.handle(any(PurchasePassCommand.class))).thenReturn(result);

        // When
        Response response = passResource.purchasePass(securityContext, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(201);

        PurchasePassResponse resultResponse = (PurchasePassResponse) response.getEntity();
        assertThat(resultResponse).isNotNull();
        assertThat(resultResponse.id()).isEqualTo(passId.toString());
        assertThat(resultResponse.semesterCode()).isEqualTo("A25");
        assertThat(resultResponse.dailyTripDurationMinutes()).isEqualTo(30);
        assertThat(resultResponse.billingMode()).isEqualTo("MONTHLY");
        assertThat(resultResponse.price()).isEqualTo(50);

        verify(purchasePassUseCase, times(1)).handle(any(PurchasePassCommand.class));
    }

    @Test
    void purchasePass_shouldThrowException_whenSemesterCodeIsInvalid() {
        // Given
        PurchasePassRequest request = new PurchasePassRequest(
            "", // Invalid semester code
            30,
            "MONTHLY"
        );

        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(purchasePassUseCase.handle(any(PurchasePassCommand.class)))
            .thenThrow(new IllegalArgumentException("Invalid semester code"));

        // When/Then
        assertThatThrownBy(() -> passResource.purchasePass(securityContext, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid semester code");
    }

    @Test
    void purchasePass_shouldThrowException_whenDailyTripDurationIsTooShort() {
        // Given
        PurchasePassRequest request = new PurchasePassRequest(
            "A25",
            5, // Invalid duration (less than 10 minutes)
            "MONTHLY"
        );

        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(purchasePassUseCase.handle(any(PurchasePassCommand.class)))
            .thenThrow(new IllegalArgumentException("Invalid trip duration"));

        // When/Then
        assertThatThrownBy(() -> passResource.purchasePass(securityContext, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid trip duration");
    }

    @Test
    void purchasePass_shouldThrowException_whenBillingModeIsInvalid() {
        // Given
        PurchasePassRequest request = new PurchasePassRequest(
            "A25",
            30,
            "" // Invalid billing mode
        );

        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(purchasePassUseCase.handle(any(PurchasePassCommand.class)))
            .thenThrow(new IllegalArgumentException("Invalid billing mode"));

        // When/Then
        assertThatThrownBy(() -> passResource.purchasePass(securityContext, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid billing mode");
    }

    @Test
    void purchasePass_shouldThrowException_whenUseCaseFails() {
        // Given
        PurchasePassRequest request = new PurchasePassRequest(
            "A25",
            30,
            "MONTHLY"
        );

        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(purchasePassUseCase.handle(any(PurchasePassCommand.class)))
            .thenThrow(new RuntimeException("Payment failed"));

        // When/Then
        assertThatThrownBy(() -> passResource.purchasePass(securityContext, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Payment failed");
    }

    @Test
    void getAccountPasses_shouldReturnPassesForRegularUser() {
        // Given
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        PassId passId = PassId.newId();
        Pass pass = new Pass(
            passId,
            AccountId.fromString(accountId),
            SemesterCode.of("A25"),
            DailyTripDuration.of(30),
            BillingMode.MONTHLY,
            5000,
            Instant.now()
        );

        Account account = new Account(
            AccountId.fromString(accountId),
            "test123",
            AccountName.of("Test User"),
            EmailAddress.of("test@example.com"),
            new PasswordHash("hashedpassword"),
            new DateOfBirth(LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.MALE),
            AccountRole.USER,
            java.time.Instant.now()
        );

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(accountRepository.findById(AccountId.fromString(accountId))).thenReturn(java.util.Optional.of(account));
        when(getAccountPassesUseCase.handle(accountId)).thenReturn(List.of(pass));

        // When
        Response response = passResource.getAccountPasses(securityContext);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);

        List<PassResponse> result = (List<PassResponse>) response.getEntity();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(passId.toString());
        assertThat(result.get(0).semesterCode()).isEqualTo("A25");

        verify(getAccountPassesUseCase, times(1)).handle(accountId);
    }

    @Test
    void getAccountPasses_shouldReturnEmployeeAccessPlan() {
        // Given
        String accountId = "456e4567-e89b-12d3-a456-426614174000";
        EffectivePass plan = new EffectivePass(
            "EMPLOYEE_ACCESS",
            SemesterCode.of("A25"),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 4, 30),
            45,
            false
        );

        Account account = new Account(
            AccountId.fromString(accountId),
            "emp123",
            AccountName.of("Employee User"),
            EmailAddress.of("employee@example.com"),
            new PasswordHash("hashedpassword"),
            new DateOfBirth(LocalDate.of(1985, 5, 15)),
            new AccountGender(AccountGender.Gender.FEMALE),
            AccountRole.EMPLOYEE,
            java.time.Instant.now()
        );

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(accountRepository.findById(AccountId.fromString(accountId))).thenReturn(java.util.Optional.of(account));
        when(accessPlanService.getEffectivePassFor(account, LocalDate.now())).thenReturn(java.util.Optional.of(plan));

        // When
        Response response = passResource.getAccountPasses(securityContext);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);

        List<PassResponse> result = (List<PassResponse>) response.getEntity();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).semesterCode()).isEqualTo("A25");
        assertThat(result.get(0).dailyTripDurationMinutes()).isEqualTo(45);

        verify(accessPlanService, times(1)).getEffectivePassFor(account, LocalDate.now());
    }

    @Test
    void getValidPass_shouldReturnValidPassForRegularUser() {
        // Given
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        PassId passId = PassId.newId();
        Pass pass = new Pass(
            passId,
            AccountId.fromString(accountId),
            SemesterCode.of("A25"),
            DailyTripDuration.of(30),
            BillingMode.MONTHLY,
            5000,
            Instant.now()
        );

        Account account = new Account(
            AccountId.fromString(accountId),
            "test123",
            AccountName.of("Test User"),
            EmailAddress.of("test@example.com"),
            new PasswordHash("hashedpassword"),
            new DateOfBirth(LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.MALE),
            AccountRole.USER,
            java.time.Instant.now()
        );

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(accountRepository.findById(AccountId.fromString(accountId))).thenReturn(java.util.Optional.of(account));
        when(getValidPassUseCase.handle(accountId)).thenReturn(java.util.Optional.of(pass));

        // When
        Response response = passResource.getValidPass(securityContext);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);

        PassResponse result = (PassResponse) response.getEntity();
        assertThat(result.id()).isEqualTo(passId.toString());
        assertThat(result.semesterCode()).isEqualTo("A25");

        verify(getValidPassUseCase, times(1)).handle(accountId);
    }

    @Test
    void getValidPass_shouldReturnNotFoundForRegularUserWithoutPass() {
        // Given
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        Account account = new Account(
            AccountId.fromString(accountId),
            "test123",
            AccountName.of("Test User"),
            EmailAddress.of("test@example.com"),
            new PasswordHash("hashedpassword"),
            new DateOfBirth(LocalDate.of(1990, 1, 1)),
            new AccountGender(AccountGender.Gender.MALE),
            AccountRole.USER,
            java.time.Instant.now()
        );

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(accountRepository.findById(AccountId.fromString(accountId))).thenReturn(java.util.Optional.of(account));
        when(getValidPassUseCase.handle(accountId)).thenReturn(java.util.Optional.empty());

        // When
        Response response = passResource.getValidPass(securityContext);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(404);

        String result = (String) response.getEntity();
        assertThat(result).contains("NO_VALID_PASS");

        verify(getValidPassUseCase, times(1)).handle(accountId);
    }

    @Test
    void getValidPass_shouldReturnEmployeeAccessPlanWhenAvailable() {
        // Given
        String accountId = "456e4567-e89b-12d3-a456-426614174000";
        EffectivePass plan = new EffectivePass(
            "EMPLOYEE_ACCESS",
            SemesterCode.of("A25"),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 4, 30),
            45,
            false
        );

        Account account = new Account(
            AccountId.fromString(accountId),
            "emp123",
            AccountName.of("Employee User"),
            EmailAddress.of("employee@example.com"),
            new PasswordHash("hashedpassword"),
            new DateOfBirth(LocalDate.of(1985, 5, 15)),
            new AccountGender(AccountGender.Gender.FEMALE),
            AccountRole.EMPLOYEE,
            java.time.Instant.now()
        );

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(accountRepository.findById(AccountId.fromString(accountId))).thenReturn(java.util.Optional.of(account));
        when(accessPlanService.getEffectivePassFor(account, LocalDate.now())).thenReturn(java.util.Optional.of(plan));

        // When
        Response response = passResource.getValidPass(securityContext);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);

        PassResponse result = (PassResponse) response.getEntity();
        assertThat(result.semesterCode()).isEqualTo("A25");
        assertThat(result.dailyTripDurationMinutes()).isEqualTo(45);

        verify(accessPlanService, times(1)).getEffectivePassFor(account, LocalDate.now());
    }

    @Test
    void getValidPass_shouldReturnNotFoundForEmployeeWithoutAccessPlan() {
        // Given
        String accountId = "456e4567-e89b-12d3-a456-426614174000";
        Account account = new Account(
            AccountId.fromString(accountId),
            "emp123",
            AccountName.of("Employee User"),
            EmailAddress.of("employee@example.com"),
            new PasswordHash("hashedpassword"),
            new DateOfBirth(LocalDate.of(1985, 5, 15)),
            new AccountGender(AccountGender.Gender.FEMALE),
            AccountRole.EMPLOYEE,
            java.time.Instant.now()
        );

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(accountRepository.findById(AccountId.fromString(accountId))).thenReturn(java.util.Optional.of(account));
        when(accessPlanService.getEffectivePassFor(account, LocalDate.now())).thenReturn(java.util.Optional.empty());

        // When
        Response response = passResource.getValidPass(securityContext);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(404);

        String result = (String) response.getEntity();
        assertThat(result).contains("NO_VALID_PASS");

        verify(accessPlanService, times(1)).getEffectivePassFor(account, LocalDate.now());
    }
}