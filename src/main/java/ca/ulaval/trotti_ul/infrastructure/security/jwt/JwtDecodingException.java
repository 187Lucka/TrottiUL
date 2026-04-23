package ca.ulaval.trotti_ul.infrastructure.security.jwt;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class JwtDecodingException extends BusinessException {

    public JwtDecodingException(String code) {
        super(code);
    }

    public JwtDecodingException(String code, String message) {
        super(code, message);
    }
}
