package ca.ulaval.trotti_ul.domain.common;

public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}
