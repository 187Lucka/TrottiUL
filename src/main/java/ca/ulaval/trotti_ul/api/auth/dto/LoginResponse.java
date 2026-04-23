package ca.ulaval.trotti_ul.api.auth.dto;

import java.time.Instant;

public record LoginResponse(
        String accessToken,
        Instant expiresAt
) {}