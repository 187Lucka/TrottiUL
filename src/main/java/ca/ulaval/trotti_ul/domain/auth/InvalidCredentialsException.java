package ca.ulaval.trotti_ul.domain.auth;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid credentials");
    }
}
