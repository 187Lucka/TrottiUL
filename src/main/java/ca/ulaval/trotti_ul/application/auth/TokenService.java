package ca.ulaval.trotti_ul.application.auth;

public interface TokenService {
    String encode(JwtClaims claims);
    DecodedToken decode(String token);
}
