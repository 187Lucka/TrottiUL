package ca.ulaval.trotti_ul.domain.technician;

import java.util.List;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.account.AccountId;

public interface TechnicianRepository {

    void save(Technician technician);

    Optional<Technician> findById(TechnicianId id);

    Optional<Technician> findByAccountId(AccountId accountId);

    List<Technician> findAllActive();

    void delete(TechnicianId id);
}