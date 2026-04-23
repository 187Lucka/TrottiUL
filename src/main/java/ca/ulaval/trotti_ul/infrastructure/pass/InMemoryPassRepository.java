package ca.ulaval.trotti_ul.infrastructure.pass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.pass.Pass;
import ca.ulaval.trotti_ul.domain.pass.PassId;
import ca.ulaval.trotti_ul.domain.pass.PassRepository;
import ca.ulaval.trotti_ul.domain.semester.SemesterCode;

public class InMemoryPassRepository implements PassRepository {

    private final Map<PassId, Pass> byId = new ConcurrentHashMap<>();
    private final Map<AccountId, List<PassId>> byAccountId = new ConcurrentHashMap<>();

    @Override
    public void save(Pass pass) {
        byId.put(pass.id(), pass);
        byAccountId.computeIfAbsent(pass.accountId(), k -> new ArrayList<>())
                .add(pass.id());
    }

    @Override
    public Optional<Pass> findById(PassId id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<Pass> findByAccountId(AccountId accountId) {
        List<PassId> passIds = byAccountId.getOrDefault(accountId, List.of());
        return passIds.stream()
                .map(byId::get)
                .filter(p -> p != null)
                .toList();
    }

    @Override
    public boolean existsByAccountIdAndSemesterCode(AccountId accountId, SemesterCode semesterCode) {
        return findByAccountId(accountId).stream()
                .anyMatch(pass -> pass.semesterCode().equals(semesterCode));
    }

    @Override
    public Optional<Pass> findByAccountAndSemester(AccountId accountId, SemesterCode semesterCode) {
        return findByAccountId(accountId).stream()
                .filter(pass -> pass.semesterCode().equals(semesterCode))
                .findFirst();
    }
}
