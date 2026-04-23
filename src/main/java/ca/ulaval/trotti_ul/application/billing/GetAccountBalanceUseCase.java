package ca.ulaval.trotti_ul.application.billing;

import java.math.BigDecimal;
import java.util.List;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.billing.Invoice;
import ca.ulaval.trotti_ul.domain.billing.InvoiceStatus;
import ca.ulaval.trotti_ul.domain.billing.InvoiceRepository;

public class GetAccountBalanceUseCase {

    private final InvoiceRepository invoiceRepository;

    public GetAccountBalanceUseCase(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public BalanceResult handle(String accountIdString) {
        AccountId accountId = AccountId.fromString(accountIdString);
        List<Invoice> invoices = invoiceRepository.findByAccountId(accountId);
        int totalCents = invoices.stream()
                .filter(inv -> inv.status() == InvoiceStatus.UNPAID)
                .mapToInt(Invoice::totalInCents)
                .sum();
        BigDecimal balance = centsToDollars(totalCents);
        return new BalanceResult(balance);
    }

    private BigDecimal centsToDollars(int cents) {
        return BigDecimal.valueOf(cents).movePointLeft(2);
    }

    public record BalanceResult(BigDecimal balance) { }
}
