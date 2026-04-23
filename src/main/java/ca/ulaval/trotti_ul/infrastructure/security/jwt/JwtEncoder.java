package ca.ulaval.trotti_ul.infrastructure.security.jwt;

import ca.ulaval.trotti_ul.application.auth.JwtClaims;

public interface JwtEncoder {
    String encode(JwtClaims claims);
}