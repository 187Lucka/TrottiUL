package ca.ulaval.trotti_ul.application.auth;

import java.time.Instant;
import java.util.Set;

public record JwtClaims(
        String subject,
        Set<String> roles,
        Instant issuedAt,
        Instant expiresAt,
        String technicianId
) {}