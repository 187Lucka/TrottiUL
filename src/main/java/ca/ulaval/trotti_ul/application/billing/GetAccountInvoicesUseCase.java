package ca.ulaval.trotti_ul.application.billing;

import java.util.List;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.billing.Invoice;
import ca.ulaval.trotti_ul.domain.billing.InvoiceRepository;

public class GetAccountInvoicesUseCase {

    private final InvoiceRepository invoiceRepository;

    public GetAccountInvoicesUseCase(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> handle(String accountIdString) {
        AccountId accountId = AccountId.fromString(accountIdString);
        return invoiceRepository.findByAccountId(accountId);
    }
}
