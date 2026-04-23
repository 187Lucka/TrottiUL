package ca.ulaval.trotti_ul.infrastructure.account;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<AccountId, Account> byId = new ConcurrentHashMap<>();
    private final Map<String, AccountId> byEmail = new ConcurrentHashMap<>();
    private final Map<String, AccountId> byIdul = new ConcurrentHashMap<>();

    @Override
    public void save(Account account) {
        byId.put(account.id(), account);
        byEmail.put(account.email().toString(), account.id());
        if (account.idul() != null) {
            byIdul.put(account.idul(), account.id());
        }
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        AccountId id = byEmail.get(email.toLowerCase());
        return id == null ? Optional.empty() : findById(id);
    }

    @Override
    public Optional<Account> findByIdul(String idul) {
        AccountId id = byIdul.get(idul);
        return id == null ? Optional.empty() : findById(id);
    }

    @Override
    public List<Account> findByIds(List<AccountId> ids) {
        return ids.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .toList();
    }
}