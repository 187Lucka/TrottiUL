package ca.ulaval.trotti_ul.application.ride;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.notification.EmailTemplateId;
import ca.ulaval.trotti_ul.domain.notification.TemplatedEmailService;
import ca.ulaval.trotti_ul.domain.pass.AccessPlanService;
import ca.ulaval.trotti_ul.domain.pass.EffectivePass;
import ca.ulaval.trotti_ul.domain.ride.GenerateCode;
import ca.ulaval.trotti_ul.domain.ride.NoValidPassForUnlockException;
import ca.ulaval.trotti_ul.domain.ride.RideCodeRepository;
import ca.ulaval.trotti_ul.domain.account.AccountNotFoundException;

public class GenerateCodeUseCase {

    private static final int VALIDITY_SECONDS = 60;

    private final AccessPlanService accessPlanService;
    private final RideCodeRepository rideCodeRepository;
    private final AccountRepository accountRepository;
    private final TemplatedEmailService emailService;
    private final Clock clock;

    public GenerateCodeUseCase(AccessPlanService accessPlanService,
                               RideCodeRepository rideCodeRepository,
                               AccountRepository accountRepository,
                               TemplatedEmailService emailService,
                               Clock clock) {
        this.accessPlanService = accessPlanService;
        this.rideCodeRepository = rideCodeRepository;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
        this.clock = clock;
    }

    public GenerateCode handle(String accountIdString) {
        AccountId accountId = AccountId.fromString(accountIdString);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));

        LocalDate today = LocalDate.now(clock);
        EffectivePass plan = accessPlanService.getEffectivePassFor(account, today)
                .orElseThrow(NoValidPassForUnlockException::new);

        Instant now = Instant.now(clock);
        GenerateCode code = GenerateCode.create(now, VALIDITY_SECONDS);
        rideCodeRepository.save(accountId, code);

        emailService.send(
                account.email().value(),
                EmailTemplateId.RIDE_UNLOCK_CODE,
                buildEmailVariables(account, plan, code)
        );

        return code;
    }

    private Map<String, String> buildEmailVariables(Account account, EffectivePass plan, GenerateCode code) {
        String dailyDuration = plan.dailyUnlimitedMinutes() + " minutes" + (plan.billable() ? "" : " (employé)");
        return Map.of(
                "firstName", account.name().value(),
                "unlockCode", code.value(),
                "expiresAt", code.expiresAt().toString(),
                "semester", plan.semesterCode().value(),
                "dailyDuration", dailyDuration
        );
    }
}
