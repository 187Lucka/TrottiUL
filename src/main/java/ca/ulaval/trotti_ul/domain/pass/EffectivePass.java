package ca.ulaval.trotti_ul.domain.pass;

import java.time.LocalDate;
import java.util.Objects;

import ca.ulaval.trotti_ul.domain.semester.SemesterCode;

public record EffectivePass(
        String source,
        SemesterCode semesterCode,
        LocalDate validFrom,
        LocalDate validUntil,
        int dailyUnlimitedMinutes,
        boolean billable
) {
    public EffectivePass {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(semesterCode, "semester code must not be null");
        Objects.requireNonNull(validFrom, "validFrom must not be null");
        Objects.requireNonNull(validUntil, "validUntil must not be null");
        if (dailyUnlimitedMinutes < 30) {
            throw new IllegalArgumentException("dailyUnlimitedMinutes must be at least 30");
        }
    }
}
