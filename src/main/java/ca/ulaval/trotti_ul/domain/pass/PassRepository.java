package ca.ulaval.trotti_ul.domain.pass;

import java.util.List;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.semester.SemesterCode;

public interface PassRepository {

    void save(Pass pass);

    Optional<Pass> findById(PassId id);

    List<Pass> findByAccountId(AccountId accountId);

    boolean existsByAccountIdAndSemesterCode(AccountId accountId, SemesterCode semesterCode);

    Optional<Pass> findByAccountAndSemester(AccountId accountId, SemesterCode semesterCode);
}
