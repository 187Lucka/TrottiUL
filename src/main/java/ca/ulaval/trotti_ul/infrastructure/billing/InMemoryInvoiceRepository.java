package ca.ulaval.trotti_ul.infrastructure.billing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.billing.Invoice;
import ca.ulaval.trotti_ul.domain.billing.InvoiceId;
import ca.ulaval.trotti_ul.domain.billing.InvoiceRepository;
import ca.ulaval.trotti_ul.domain.pass.PassId;

public class InMemoryInvoiceRepository implements InvoiceRepository {

    private final Map<InvoiceId, Invoice> byId = new ConcurrentHashMap<>();
    private final Map<PassId, InvoiceId> byPassId = new ConcurrentHashMap<>();
    private final Map<AccountId, List<InvoiceId>> byAccountId = new ConcurrentHashMap<>();

    @Override
    public void save(Invoice invoice) {
        byId.put(invoice.id(), invoice);
        if (invoice.passId() != null) {
            byPassId.put(invoice.passId(), invoice.id());
        }
        byAccountId.computeIfAbsent(invoice.accountId(), k -> new ArrayList<>())
                .add(invoice.id());
    }

    @Override
    public Optional<Invoice> findById(InvoiceId id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<Invoice> findByPassId(PassId passId) {
        if (passId == null) {
            return Optional.empty();
        }
        InvoiceId invoiceId = byPassId.get(passId);
        if (invoiceId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byId.get(invoiceId));
    }

    @Override
    public List<Invoice> findByAccountId(AccountId accountId) {
        List<InvoiceId> invoiceIds = byAccountId.getOrDefault(accountId, List.of());
        return invoiceIds.stream()
                .map(byId::get)
                .filter(i -> i != null)
                .toList();
    }
}
