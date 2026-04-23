package ca.ulaval.trotti_ul.domain.account;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class AccountAlreadyExistsException extends BusinessException {

    public AccountAlreadyExistsException(String code, String message) {
        super(code, message);
    }

    public static AccountAlreadyExistsException forEmail(String email) {
        return new AccountAlreadyExistsException(
                "ACCOUNT_ALREADY_EXISTS_EMAIL",
                "Account already exists with this email: " + email
        );
    }

    public static AccountAlreadyExistsException forIdul(String idul) {
        return new AccountAlreadyExistsException(
                "ACCOUNT_ALREADY_EXISTS_IDUL",
                "Account already exists with this idul: " + idul
        );
    }
}
