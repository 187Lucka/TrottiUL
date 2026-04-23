package ca.ulaval.trotti_ul.domain.billing;

import java.util.List;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.pass.PassId;

public interface InvoiceRepository {

    void save(Invoice invoice);

    Optional<Invoice> findById(InvoiceId id);

    Optional<Invoice> findByPassId(PassId passId);

    List<Invoice> findByAccountId(AccountId accountId);
}
