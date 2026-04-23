package ca.ulaval.trotti_ul.infrastructure.technician;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianId;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;

public class InMemoryTechnicianRepository implements TechnicianRepository {

    private final Map<TechnicianId, Technician> byId = new ConcurrentHashMap<>();
    private final Map<AccountId, TechnicianId> byAccount = new ConcurrentHashMap<>();

    @Override
    public void save(Technician technician) {
        byId.put(technician.id(), technician);
        byAccount.put(technician.accountId(), technician.id());
    }

    @Override
    public Optional<Technician> findById(TechnicianId id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<Technician> findByAccountId(AccountId accountId) {
        TechnicianId id = byAccount.get(accountId);
        return id == null ? Optional.empty() : findById(id);
    }

    @Override
    public List<Technician> findAllActive() {
        return byId.values().stream()
                .filter(Technician::isActive)
                .toList();
    }

    @Override
    public void delete(TechnicianId id) {
        Technician tech = byId.remove(id);
        if (tech != null) {
            byAccount.remove(tech.accountId());
        }
    }
}