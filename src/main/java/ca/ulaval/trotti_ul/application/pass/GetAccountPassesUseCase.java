package ca.ulaval.trotti_ul.application.pass;

import java.util.List;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.pass.Pass;
import ca.ulaval.trotti_ul.domain.pass.PassRepository;

public class GetAccountPassesUseCase {

    private final PassRepository passRepository;

    public GetAccountPassesUseCase(PassRepository passRepository) {
        this.passRepository = passRepository;
    }

    public List<Pass> handle(String accountIdString) {
        AccountId accountId = AccountId.fromString(accountIdString);
        return passRepository.findByAccountId(accountId);
    }
}
