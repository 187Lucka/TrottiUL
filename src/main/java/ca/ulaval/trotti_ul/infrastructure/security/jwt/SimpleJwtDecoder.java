package ca.ulaval.trotti_ul.infrastructure.security.jwt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class SimpleJwtDecoder implements JwtDecoder {

    private final SecretKey secretKey;

    public SimpleJwtDecoder(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public DecodedJwt decode(String token) throws JwtDecodingException {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();

            @SuppressWarnings("unchecked")
            List<String> rolesList = claims.get("roles", List.class);
            Set<String> roles = rolesList != null ? new HashSet<>(rolesList) : Set.of();

            String technicianId = claims.get("technicianId", String.class);

            return new DecodedJwt(
                    subject,
                    roles,
                    claims.getIssuedAt().toInstant(),
                    claims.getExpiration().toInstant(),
                    technicianId
            );
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtDecodingException("INVALID_TOKEN", "Invalid or expired token");
        }
    }
}
