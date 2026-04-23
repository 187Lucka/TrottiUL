package ca.ulaval.trotti_ul.infrastructure.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import ca.ulaval.trotti_ul.application.auth.JwtClaims;
import io.jsonwebtoken.Jwts;

public class SimpleJwtEncoder implements JwtEncoder {

    private final SecretKey secretKey;

    public SimpleJwtEncoder(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String encode(JwtClaims claims) {
        var builder = Jwts.builder()
                .subject(claims.subject())
                .claim("roles", claims.roles())
                .issuedAt(Date.from(claims.issuedAt()))
                .expiration(Date.from(claims.expiresAt()));

        if (claims.technicianId() != null) {
            builder.claim("technicianId", claims.technicianId());
        }

        return builder.signWith(secretKey).compact();
    }
}
