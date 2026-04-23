package ca.ulaval.trotti_ul.api.pass.dto;

import ca.ulaval.trotti_ul.domain.pass.Pass;

public record PurchasePassResponse(
        String id,
        String semesterCode,
        int dailyTripDurationMinutes,
        String billingMode,
        int price,
        String purchasedAt
) {
    public static PurchasePassResponse from(Pass pass) {
        return new PurchasePassResponse(
                pass.id().toString(),
                pass.semesterCode().value(),
                pass.dailyTripDuration().minutes(),
                pass.billingMode().name(),
                pass.priceInDollars().intValueExact(),
                pass.purchasedAt().toString()
        );
    }
}
