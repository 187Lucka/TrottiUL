package ca.ulaval.trotti_ul.application.pass;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.pass.Pass;
import ca.ulaval.trotti_ul.domain.pass.PassRepository;
import ca.ulaval.trotti_ul.domain.semester.Semester;
import ca.ulaval.trotti_ul.domain.semester.SemesterCatalog;

public class GetValidPassUseCase {

    private final PassRepository passRepository;
    private final SemesterCatalog semesterCatalog;
    private final Clock clock;

    public GetValidPassUseCase(PassRepository passRepository,
                               SemesterCatalog semesterCatalog,
                               Clock clock) {
        this.passRepository = passRepository;
        this.semesterCatalog = semesterCatalog;
        this.clock = clock;
    }

    public Optional<Pass> handle(String accountIdString) {
        AccountId accountId = AccountId.fromString(accountIdString);
        LocalDate today = LocalDate.now(clock);

        Optional<Semester> currentSemester = semesterCatalog.findCurrentSemester(today);
        if (currentSemester.isEmpty()) {
            return Optional.empty();
        }

        return passRepository.findByAccountId(accountId).stream()
                .filter(pass -> pass.isValidFor(currentSemester.get(), today))
                .findFirst();
    }
}
