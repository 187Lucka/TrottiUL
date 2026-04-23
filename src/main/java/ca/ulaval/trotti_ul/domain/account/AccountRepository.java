package ca.ulaval.trotti_ul.domain.account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    void save(Account account);

    Optional<Account> findById(AccountId id);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByIdul(String idul);

    List<Account> findByIds(List<AccountId> ids);
}