package ca.ulaval.trotti_ul.infrastructure.billing;

import java.time.Clock;
import java.time.Instant;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.billing.Invoice;
import ca.ulaval.trotti_ul.domain.billing.InvoiceRepository;
import ca.ulaval.trotti_ul.domain.billing.InvoiceStatus;
import ca.ulaval.trotti_ul.domain.billing.RideBillingService;
import ca.ulaval.trotti_ul.domain.pass.BillingMode;
import ca.ulaval.trotti_ul.domain.pass.Pass;
import ca.ulaval.trotti_ul.domain.payment.CreditCard;
import ca.ulaval.trotti_ul.domain.payment.CreditCardRepository;
import ca.ulaval.trotti_ul.domain.payment.PaymentGateway;
import ca.ulaval.trotti_ul.domain.payment.PaymentResult;

public class RideBillingAdapter implements RideBillingService {

    private final InvoiceRepository invoiceRepository;
    private final CreditCardRepository creditCardRepository;
    private final PaymentGateway paymentGateway;
    private final Clock clock;

    public RideBillingAdapter(InvoiceRepository invoiceRepository,
                              CreditCardRepository creditCardRepository,
                              PaymentGateway paymentGateway,
                              Clock clock) {
        this.invoiceRepository = invoiceRepository;
        this.creditCardRepository = creditCardRepository;
        this.paymentGateway = paymentGateway;
        this.clock = clock;
    }

    @Override
    public void chargeRideOverage(AccountId accountId, Pass pass, int amountInCents, String rideId) {
        if (amountInCents <= 0) {
            return;
        }

        BillingMode mode = pass != null ? pass.billingMode() : BillingMode.MONTHLY;
        boolean monthly = mode == BillingMode.MONTHLY;
        Instant now = Instant.now(clock);

        InvoiceStatus status = InvoiceStatus.UNPAID;
        String transactionId = rideId;

        if (!monthly) {
            CreditCard card = creditCardRepository.findByAccountId(accountId).orElse(null);
            if (card != null) {
                PaymentResult result = paymentGateway.processPayment(card, amountInCents);
                if (result.isSuccess()) {
                    status = InvoiceStatus.PAID;
                    transactionId = result.transactionId();
                }
            }
        }

        Invoice invoice = Invoice.createRideOverage(
                accountId,
                pass != null ? pass.id() : null,
                pass != null ? pass.semesterCode() : null,
                amountInCents,
                transactionId,
                monthly,
                status,
                now
        );
        invoiceRepository.save(invoice);
    }
}
