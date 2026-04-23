package ca.ulaval.trotti_ul.infrastructure.security.jwt;

public interface JwtDecoder {
    DecodedJwt decode(String token) throws JwtDecodingException;
}