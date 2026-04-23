package ca.ulaval.trotti_ul.domain.pass;

import java.math.BigDecimal;

public final class PassPricing {

    private static final BigDecimal BASE_PRICE_DOLLARS = new BigDecimal("45.00");
    private static final BigDecimal EXTRA_COST_PER_10MIN_DOLLARS = new BigDecimal("2.00");
    private static final int DURATION_INCREMENT = 10;

    private PassPricing() {
    }

    public static BigDecimal calculatePrice(DailyTripDuration duration) {
        int extraMinutes = duration.extraMinutesOverBase();
        int extraBlocks = extraMinutes / DURATION_INCREMENT;
        BigDecimal extra = EXTRA_COST_PER_10MIN_DOLLARS.multiply(BigDecimal.valueOf(extraBlocks));
        return BASE_PRICE_DOLLARS.add(extra);
    }

    public static int calculateInCents(DailyTripDuration duration) {
        return toCents(calculatePrice(duration));
    }

    public static BigDecimal basePriceInDollars() {
        return BASE_PRICE_DOLLARS;
    }

    public static BigDecimal extraCostPer10MinInDollars() {
        return EXTRA_COST_PER_10MIN_DOLLARS;
    }

    public static int toCents(BigDecimal dollars) {
        return dollars.movePointRight(2).intValueExact();
    }

    public static BigDecimal centsToDollars(int cents) {
        return BigDecimal.valueOf(cents).movePointLeft(2);
    }
}
