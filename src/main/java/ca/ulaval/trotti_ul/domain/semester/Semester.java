package ca.ulaval.trotti_ul.domain.semester;

import java.time.LocalDate;
import java.util.Objects;

public class Semester {

    private final SemesterCode code;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Semester(SemesterCode code, LocalDate startDate, LocalDate endDate) {
        this.code = Objects.requireNonNull(code, "Semester code must not be null");
        this.startDate = Objects.requireNonNull(startDate, "Start date must not be null");
        this.endDate = Objects.requireNonNull(endDate, "End date must not be null");

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }
    }

    public SemesterCode code() {
        return code;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    public boolean isActive(LocalDate today) {
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public boolean isPurchasable(LocalDate today) {
        return !today.isAfter(endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Semester semester = (Semester) o;
        return Objects.equals(code, semester.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
