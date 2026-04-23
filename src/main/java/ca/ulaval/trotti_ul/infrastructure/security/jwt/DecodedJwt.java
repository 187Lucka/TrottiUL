package ca.ulaval.trotti_ul.infrastructure.security.jwt;

import java.time.Instant;
import java.util.Set;

public record DecodedJwt(
        String subject,
        Set<String> roles,
        Instant issuedAt,
        Instant expiresAt,
        String technicianId
) {}