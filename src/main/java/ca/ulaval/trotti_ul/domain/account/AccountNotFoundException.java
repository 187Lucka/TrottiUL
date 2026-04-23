package ca.ulaval.trotti_ul.domain.account;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String accountId) {
        super("ACCOUNT_NOT_FOUND", "Account not found for id " + accountId);
    }
}
