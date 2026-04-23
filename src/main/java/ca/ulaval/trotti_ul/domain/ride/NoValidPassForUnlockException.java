package ca.ulaval.trotti_ul.domain.ride;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class NoValidPassForUnlockException extends BusinessException {
    public NoValidPassForUnlockException() {
        super("NO_VALID_PASS", "No valid pass for the current semester");
    }
}
