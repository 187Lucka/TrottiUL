package ca.ulaval.trotti_ul.api.semester.dto;

import java.time.LocalDate;

import ca.ulaval.trotti_ul.domain.semester.Semester;

public record SemesterResponse(
        String semesterCode,
        LocalDate startDate,
        LocalDate endDate,
        boolean isActive,
        boolean isPurchasable
) {
    public static SemesterResponse from(Semester semester, LocalDate today) {
        return new SemesterResponse(
                semester.code().value(),
                semester.startDate(),
                semester.endDate(),
                semester.isActive(today),
                semester.isPurchasable(today)
        );
    }
}
