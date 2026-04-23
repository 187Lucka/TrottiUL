package ca.ulaval.trotti_ul.domain.pass;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.semester.Semester;
import ca.ulaval.trotti_ul.domain.semester.SemesterCatalog;

public class AccessPlanService {

    private static final int EMPLOYEE_DAILY_MINUTES = 30;

    private final SemesterCatalog semesterCatalog;
    private final PassRepository passRepository;

    public AccessPlanService(SemesterCatalog semesterCatalog, PassRepository passRepository) {
        this.semesterCatalog = Objects.requireNonNull(semesterCatalog);
        this.passRepository = Objects.requireNonNull(passRepository);
    }

    public Optional<EffectivePass> getEffectivePassFor(Account account, LocalDate date) {
        Optional<Semester> maybeSemester = semesterCatalog.findCurrentSemester(date);
        if (maybeSemester.isEmpty()) {
            return Optional.empty();
        }
        Semester semester = maybeSemester.get();

        if (account.isEmployee()) {
            return Optional.of(new EffectivePass(
                    "EMPLOYEE_FREE",
                    semester.code(),
                    semester.startDate(),
                    semester.endDate(),
                    EMPLOYEE_DAILY_MINUTES,
                    false
            ));
        }

        return passRepository.findByAccountAndSemester(account.id(), semester.code())
                .map(pass -> new EffectivePass(
                        "PAID_PASS",
                        pass.semesterCode(),
                        semester.startDate(),
                        semester.endDate(),
                        pass.dailyTripDuration().minutes(),
                        true
                ));
    }
}
