package ca.ulaval.trotti_ul.application.ride;

import java.time.Clock;
import java.time.LocalDate;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountNotFoundException;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.pass.AccessPlanNotFoundException;
import ca.ulaval.trotti_ul.domain.pass.AccessPlanService;
import ca.ulaval.trotti_ul.domain.pass.EffectivePass;

public class StartRideWithAccessPlanUseCase {

    private final AccountRepository accountRepository;
    private final AccessPlanService accessPlanService;
    private final Clock clock;

    public StartRideWithAccessPlanUseCase(AccountRepository accountRepository,
                                          AccessPlanService accessPlanService,
                                          Clock clock) {
        this.accountRepository = accountRepository;
        this.accessPlanService = accessPlanService;
        this.clock = clock;
    }

    public void handle(String accountIdString) {
        AccountId accountId = AccountId.fromString(accountIdString);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));

        LocalDate today = LocalDate.now(clock);
        EffectivePass plan = accessPlanService.getEffectivePassFor(account, today)
                .orElseThrow(AccessPlanNotFoundException::new);


        throw new UnsupportedOperationException("Start ride flow not implemented in this example");
    }
}
