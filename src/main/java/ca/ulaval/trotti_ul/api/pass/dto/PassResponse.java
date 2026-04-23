package ca.ulaval.trotti_ul.api.pass.dto;

import java.time.Instant;
import java.time.ZoneOffset;

import ca.ulaval.trotti_ul.domain.pass.Pass;
import ca.ulaval.trotti_ul.domain.pass.EffectivePass;

public record PassResponse(
        String id,
        String semesterCode,
        int dailyTripDurationMinutes,
        String billingMode,
        int price,
        Instant purchasedAt
) {
    public static PassResponse from(Pass pass) {
        return new PassResponse(
                pass.id().toString(),
                pass.semesterCode().value(),
                pass.dailyTripDuration().minutes(),
                pass.billingMode().name(),
                pass.priceInDollars().intValueExact(),
                pass.purchasedAt()
        );
    }

    public static PassResponse fromEffective(String syntheticId, EffectivePass plan) {
        Instant purchasedAt = plan.validFrom().atStartOfDay().toInstant(ZoneOffset.UTC);
        return new PassResponse(
                syntheticId,
                plan.semesterCode().value(),
                plan.dailyUnlimitedMinutes(),
                plan.billable() ? "PAID" : "FREE",
                0,
                purchasedAt
        );
    }
}
