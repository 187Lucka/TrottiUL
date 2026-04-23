package ca.ulaval.trotti_ul.domain.pass;

import java.time.Instant;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.billing.Invoice;
import ca.ulaval.trotti_ul.domain.billing.InvoiceId;
import ca.ulaval.trotti_ul.domain.billing.InvoiceLine;
import ca.ulaval.trotti_ul.domain.pass.PassPricing;
import java.util.ArrayList;
import java.util.List;
import ca.ulaval.trotti_ul.domain.payment.CreditCard;
import ca.ulaval.trotti_ul.domain.payment.PaymentFailedException;
import ca.ulaval.trotti_ul.domain.payment.PaymentGateway;
import ca.ulaval.trotti_ul.domain.payment.PaymentResult;
import ca.ulaval.trotti_ul.domain.billing.InvoiceStatus;

public class BillingPolicy {

    public Invoice chargePass(AccountId accountId,
                              Pass pass,
                              CreditCard creditCard,
                              PaymentGateway paymentGateway,
                              Instant now) {
        PaymentResult paymentResult = paymentGateway.processPayment(creditCard, pass.priceInCents());

        if (!paymentResult.isSuccess()) {
            throw new PaymentFailedException(paymentResult.message());
        }

        return Invoice.createForPass(
                accountId,
                pass.id(),
                pass.semesterCode(),
                pass.dailyTripDuration(),
                paymentResult.transactionId(),
                now
        );
    }

    public Invoice createMonthlyInvoice(AccountId accountId,
                                        Pass pass,
                                        Instant now) {
        InvoiceLine base = InvoiceLine.basePass();
        int extraMinutes = pass.dailyTripDuration().extraMinutesOverBase();
        int extraCost = 0;
        if (extraMinutes > 0) {
            extraCost = (extraMinutes / 10) * PassPricing.toCents(PassPricing.extraCostPer10MinInDollars());
        }
        List<InvoiceLine> lines = new ArrayList<>();
        lines.add(base);
        if (extraMinutes > 0) {
            lines.add(InvoiceLine.extraDuration(extraMinutes, extraCost));
        }
        int total = lines.stream().mapToInt(InvoiceLine::amountInCents).sum();
        return new Invoice(
                InvoiceId.newId(),
                accountId,
                pass.id(),
                pass.semesterCode(),
                lines,
                total,
                "MONTHLY_BILLING",
                now,
                InvoiceStatus.UNPAID
        );
    }
}
