package ca.ulaval.trotti_ul.domain.semester;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SemesterCatalog {

    List<Semester> findAll();

    Optional<Semester> findByCode(SemesterCode code);

    Optional<Semester> findCurrentSemester(LocalDate today);

    List<Semester> findPurchasableSemesters(LocalDate today);
}
