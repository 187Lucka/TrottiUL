package ca.ulaval.trotti_ul.domain.pass;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.semester.Semester;
import ca.ulaval.trotti_ul.domain.semester.SemesterCode;

public class Pass {

    private final PassId id;
    private final AccountId accountId;
    private final SemesterCode semesterCode;
    private final DailyTripDuration dailyTripDuration;
    private final BillingMode billingMode;
    private final int priceInCents;
    private final Instant purchasedAt;

    public Pass(PassId id,
                AccountId accountId,
                SemesterCode semesterCode,
                DailyTripDuration dailyTripDuration,
                BillingMode billingMode,
                int priceInCents,
                Instant purchasedAt) {
        this.id = Objects.requireNonNull(id);
        this.accountId = Objects.requireNonNull(accountId);
        this.semesterCode = Objects.requireNonNull(semesterCode);
        this.dailyTripDuration = Objects.requireNonNull(dailyTripDuration);
        this.billingMode = Objects.requireNonNull(billingMode);
        this.priceInCents = priceInCents;
        this.purchasedAt = Objects.requireNonNull(purchasedAt);
    }

    public static Pass create(AccountId accountId,
                              SemesterCode semesterCode,
                              DailyTripDuration dailyTripDuration,
                              BillingMode billingMode,
                              Instant now) {
        BigDecimal priceDollars = PassPricing.calculatePrice(dailyTripDuration);
        int price = PassPricing.toCents(priceDollars);
        return new Pass(
                PassId.newId(),
                accountId,
                semesterCode,
                dailyTripDuration,
                billingMode,
                price,
                now
        );
    }

    public PassId id() {
        return id;
    }

    public AccountId accountId() {
        return accountId;
    }

    public SemesterCode semesterCode() {
        return semesterCode;
    }

    public DailyTripDuration dailyTripDuration() {
        return dailyTripDuration;
    }

    public BillingMode billingMode() {
        return billingMode;
    }

    public int priceInCents() {
        return priceInCents;
    }

    public BigDecimal priceInDollars() {
        return PassPricing.centsToDollars(priceInCents);
    }

    public Instant purchasedAt() {
        return purchasedAt;
    }

    public boolean isValidFor(Semester semester, LocalDate today) {
        return this.semesterCode.equals(semester.code()) && semester.isActive(today);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pass pass = (Pass) o;
        return Objects.equals(id, pass.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
