package ca.ulaval.trotti_ul.api.ride.dto;

import java.time.Instant;

import ca.ulaval.trotti_ul.domain.ride.GenerateCode;

public record GenerateCodeResponse(
        String code,
        Instant expiresAt
) {
    public static GenerateCodeResponse from(GenerateCode code) {
        return new GenerateCodeResponse(code.value(), code.expiresAt());
    }
}
