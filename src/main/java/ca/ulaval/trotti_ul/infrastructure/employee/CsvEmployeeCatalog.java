package ca.ulaval.trotti_ul.infrastructure.employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import ca.ulaval.trotti_ul.domain.employee.EmployeeCatalog;
import ca.ulaval.trotti_ul.domain.common.TechnicalException;

public class CsvEmployeeCatalog implements EmployeeCatalog {

    private final Set<String> employeeIduls;

    public CsvEmployeeCatalog(Path csvPath) {
        this.employeeIduls = loadFromCsv(csvPath);
    }

    public CsvEmployeeCatalog(String classpathResource) {
        this.employeeIduls = loadFromResource(classpathResource);
    }

    private Set<String> loadFromCsv(Path path) {
        Set<String> set = new HashSet<>();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    set.add(trimmed.toUpperCase());
                }
            }
        } catch (IOException e) {
            throw new TechnicalException("Failed to load employee CSV: " + path, e);
        }
        return set;
    }

    private Set<String> loadFromResource(String resourcePath) {
        Set<String> set = new HashSet<>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new TechnicalException("Resource not found on classpath: " + resourcePath);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        set.add(trimmed.toUpperCase());
                    }
                }
            }
        } catch (IOException e) {
            throw new TechnicalException("Failed to load employee CSV resource: " + resourcePath, e);
        }
        return set;
    }

    @Override
    public boolean existsByIdul(String idul) {
        if (idul == null) return false;
        return employeeIduls.contains(idul.toUpperCase());
    }
}
