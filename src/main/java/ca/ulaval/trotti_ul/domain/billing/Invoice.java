package ca.ulaval.trotti_ul.domain.billing;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.pass.DailyTripDuration;
import ca.ulaval.trotti_ul.domain.pass.PassId;
import ca.ulaval.trotti_ul.domain.pass.PassPricing;
import ca.ulaval.trotti_ul.domain.semester.SemesterCode;

public class Invoice {

    private final InvoiceId id;
    private final AccountId accountId;
    private final PassId passId;
    private final SemesterCode semesterCode;
    private final List<InvoiceLine> lines;
    private final int totalInCents;
    private final String transactionId;
    private final Instant createdAt;
    private final InvoiceStatus status;

    public Invoice(InvoiceId id,
                   AccountId accountId,
                   PassId passId,
                   SemesterCode semesterCode,
                   List<InvoiceLine> lines,
                   int totalInCents,
                   String transactionId,
                   Instant createdAt,
                   InvoiceStatus status) {
        this.id = Objects.requireNonNull(id);
        this.accountId = Objects.requireNonNull(accountId);
        this.passId = passId;
        this.semesterCode = semesterCode;
        this.lines = new ArrayList<>(lines);
        this.totalInCents = totalInCents;
        this.transactionId = Objects.requireNonNull(transactionId);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.status = Objects.requireNonNull(status);
    }

    public static Invoice createForPass(AccountId accountId,
                                        PassId passId,
                                        SemesterCode semesterCode,
                                        DailyTripDuration duration,
                                        String transactionId,
                                        Instant now) {
        List<InvoiceLine> lines = new ArrayList<>();

        lines.add(InvoiceLine.basePass());

        int extraMinutes = duration.extraMinutesOverBase();
        if (extraMinutes > 0) {
            int extraCost = (extraMinutes / 10) * PassPricing.toCents(PassPricing.extraCostPer10MinInDollars());
            lines.add(InvoiceLine.extraDuration(extraMinutes, extraCost));
        }

        int total = lines.stream().mapToInt(InvoiceLine::amountInCents).sum();

        return new Invoice(
                InvoiceId.newId(),
                accountId,
                passId,
                semesterCode,
                lines,
                total,
                transactionId,
                now,
                InvoiceStatus.PAID
        );
    }

    public static Invoice createRideOverage(AccountId accountId,
                                            PassId passId,
                                            SemesterCode semesterCode,
                                            int amountInCents,
                                            String rideId,
                                            boolean monthly,
                                            InvoiceStatus status,
                                            Instant now) {
        List<InvoiceLine> lines = List.of(InvoiceLine.rideOverage(amountInCents, rideId, monthly));
        return new Invoice(
                InvoiceId.newId(),
                accountId,
                passId,
                semesterCode,
                lines,
                amountInCents,
                rideId,
                now,
                status
        );
    }

    public InvoiceId id() {
        return id;
    }

    public AccountId accountId() {
        return accountId;
    }

    public PassId passId() {
        return passId;
    }

    public SemesterCode semesterCode() {
        return semesterCode;
    }

    public List<InvoiceLine> lines() {
        return Collections.unmodifiableList(lines);
    }

    public int totalInCents() {
        return totalInCents;
    }

    public String transactionId() {
        return transactionId;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public InvoiceStatus status() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
