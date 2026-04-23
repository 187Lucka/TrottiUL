package ca.ulaval.trotti_ul.application.pass;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountNotFoundException;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.billing.Invoice;
import ca.ulaval.trotti_ul.domain.billing.InvoiceRepository;
import ca.ulaval.trotti_ul.domain.notification.EmailTemplateId;
import ca.ulaval.trotti_ul.domain.notification.TemplatedEmailService;
import ca.ulaval.trotti_ul.domain.pass.BillingMode;
import ca.ulaval.trotti_ul.domain.pass.BillingPolicy;
import ca.ulaval.trotti_ul.domain.pass.DailyTripDuration;
import ca.ulaval.trotti_ul.domain.pass.Pass;
import ca.ulaval.trotti_ul.domain.pass.PassAlreadyExistsException;
import ca.ulaval.trotti_ul.domain.pass.PassPurchasePolicy;
import ca.ulaval.trotti_ul.domain.pass.PassRepository;
import ca.ulaval.trotti_ul.domain.payment.CreditCard;
import ca.ulaval.trotti_ul.domain.payment.CreditCardRepository;
import ca.ulaval.trotti_ul.domain.payment.NoCreditCardException;
import ca.ulaval.trotti_ul.domain.payment.PaymentFailedException;
import ca.ulaval.trotti_ul.domain.payment.PaymentGateway;
import ca.ulaval.trotti_ul.domain.semester.InvalidSemesterException;
import ca.ulaval.trotti_ul.domain.semester.Semester;
import ca.ulaval.trotti_ul.domain.semester.SemesterCatalog;
import ca.ulaval.trotti_ul.domain.semester.SemesterCode;

public class PurchasePassUseCase {

    private final SemesterCatalog semesterCatalog;
    private final PassRepository passRepository;
    private final CreditCardRepository creditCardRepository;
    private final PaymentGateway paymentGateway;
    private final InvoiceRepository invoiceRepository;
    private final AccountRepository accountRepository;
    private final TemplatedEmailService emailService;
    private final PassPurchasePolicy passPurchasePolicy;
    private final BillingPolicy billingPolicy;
    private final Clock clock;

    public PurchasePassUseCase(SemesterCatalog semesterCatalog,
                               PassRepository passRepository,
                               CreditCardRepository creditCardRepository,
                               PaymentGateway paymentGateway,
                               InvoiceRepository invoiceRepository,
                               AccountRepository accountRepository,
                               TemplatedEmailService emailService,
                               Clock clock) {
        this.semesterCatalog = semesterCatalog;
        this.passRepository = passRepository;
        this.creditCardRepository = creditCardRepository;
        this.paymentGateway = paymentGateway;
        this.invoiceRepository = invoiceRepository;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
        this.passPurchasePolicy = new PassPurchasePolicy();
        this.billingPolicy = new BillingPolicy();
        this.clock = clock;
    }

    public PurchasePassResult handle(PurchasePassCommand command) {
        AccountId accountId = AccountId.fromString(command.accountId());
        SemesterCode semesterCode = SemesterCode.of(command.semesterCode());
        DailyTripDuration duration = DailyTripDuration.of(command.dailyTripDurationMinutes());
        BillingMode billingMode = BillingMode.fromString(command.billingMode());

        LocalDate today = LocalDate.now(clock);
        Instant now = Instant.now(clock);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));

        Semester semester = semesterCatalog.findByCode(semesterCode)
                .orElseThrow(() -> new InvalidSemesterException(semesterCode));

        if (passRepository.existsByAccountIdAndSemesterCode(accountId, semesterCode)) {
            throw new PassAlreadyExistsException(semesterCode);
        }

        CreditCard creditCard = creditCardRepository.findByAccountId(accountId)
                .orElseThrow(NoCreditCardException::new);

        if (creditCard.isExpired(YearMonth.now(clock))) {
            throw new PaymentFailedException("Credit card is expired");
        }

        Pass pass = passPurchasePolicy.createPass(account, semester, duration, billingMode, today, now);

        Invoice invoice;
        if (pass.billingMode() == BillingMode.MONTHLY) {
            invoice = billingPolicy.createMonthlyInvoice(accountId, pass, now);
        } else {
            invoice = billingPolicy.chargePass(accountId, pass, creditCard, paymentGateway, now);
        }

        passRepository.save(pass);
        invoiceRepository.save(invoice);

        emailService.send(
                account.email().value(),
                EmailTemplateId.PASS_ACTIVATION,
                buildActivationEmailVariables(account, pass)
        );

        return new PurchasePassResult(pass, invoice);
    }

    private Map<String, String> buildActivationEmailVariables(Account account, Pass pass) {
        return Map.of(
                "firstName", account.name().value(),
                "semester", pass.semesterCode().value(),
                "dailyMinutes", String.valueOf(pass.dailyTripDuration().minutes()),
                "billingMode", pass.billingMode().name(),
                "price", pass.priceInDollars().toPlainString(),
                "purchasedAt", pass.purchasedAt().toString()
        );
    }
}
