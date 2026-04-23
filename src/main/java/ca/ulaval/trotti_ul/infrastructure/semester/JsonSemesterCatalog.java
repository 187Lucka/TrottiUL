package ca.ulaval.trotti_ul.infrastructure.semester;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.ulaval.trotti_ul.domain.semester.Semester;
import ca.ulaval.trotti_ul.domain.semester.SemesterCatalog;
import ca.ulaval.trotti_ul.domain.semester.SemesterCode;

public class JsonSemesterCatalog implements SemesterCatalog {

    private final List<Semester> semesters;

    public JsonSemesterCatalog() {
        this.semesters = loadFromClasspath("semesters.json");
    }

    public JsonSemesterCatalog(String resourcePath) {
        this.semesters = loadFromClasspath(resourcePath);
    }

    private List<Semester> loadFromClasspath(String resourcePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Resource not found: " + resourcePath);
            }

            ObjectMapper mapper = new ObjectMapper();
            List<SemesterJson> jsonList = mapper.readValue(is, new TypeReference<List<SemesterJson>>() {});

            List<Semester> result = new ArrayList<>();
            for (SemesterJson json : jsonList) {
                result.add(new Semester(
                        SemesterCode.of(json.semesterCode()),
                        LocalDate.parse(json.startDate()),
                        LocalDate.parse(json.endDate())
                ));
            }
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load semesters from " + resourcePath, e);
        }
    }

    @Override
    public List<Semester> findAll() {
        return new ArrayList<>(semesters);
    }

    @Override
    public Optional<Semester> findByCode(SemesterCode code) {
        return semesters.stream()
                .filter(s -> s.code().equals(code))
                .findFirst();
    }

    @Override
    public Optional<Semester> findCurrentSemester(LocalDate today) {
        return semesters.stream()
                .filter(s -> s.isActive(today))
                .findFirst();
    }

    @Override
    public List<Semester> findPurchasableSemesters(LocalDate today) {
        return semesters.stream()
                .filter(s -> s.isPurchasable(today))
                .toList();
    }

    private record SemesterJson(
            String semester_code,
            String start_date,
            String end_date
    ) {
        String semesterCode() {
            return semester_code;
        }

        String startDate() {
            return start_date;
        }

        String endDate() {
            return end_date;
        }
    }
}
