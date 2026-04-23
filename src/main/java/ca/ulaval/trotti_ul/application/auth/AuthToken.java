package ca.ulaval.trotti_ul.application.auth;

import java.time.Instant;

public record AuthToken(
        String accessToken,
        Instant expiresAt
) {}