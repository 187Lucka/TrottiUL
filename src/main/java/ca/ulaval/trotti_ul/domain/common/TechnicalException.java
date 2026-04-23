package ca.ulaval.trotti_ul.domain.common;

public class TechnicalException extends BusinessException {
    public TechnicalException(String message) {
        super("TECHNICAL_ERROR", message);
    }

    public TechnicalException(String message, Throwable cause) {
        super("TECHNICAL_ERROR", message);
        initCause(cause);
    }
}
