package ca.ulaval.trotti_ul.domain.account;

import java.time.Instant;
import java.util.Objects;

import ca.ulaval.trotti_ul.domain.common.ValidationException;

public class Account {

    private final AccountId id;
    private final String idul;
    private final AccountName name;
    private final EmailAddress email;
    private final PasswordHash passwordHash;
    private final DateOfBirth dateOfBirth;
    private final AccountGender gender;
    private final AccountRole role;
    private final Instant createdAt;

    public Account(AccountId id,
                   String idul,
                   AccountName name,
                   EmailAddress email,
                   PasswordHash passwordHash,
                   DateOfBirth dateOfBirth,
                   AccountGender gender,
                   AccountRole role,
                   Instant createdAt) {

        this.id = Objects.requireNonNull(id);
        this.idul = requireIdul(idul);
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth);
        this.gender = Objects.requireNonNull(gender);
        this.role = Objects.requireNonNull(role);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Account createUser(String idul,
                                     String name,
                                     String email,
                                     PasswordHash passwordHash,
                                     DateOfBirth dateOfBirth,
                                     AccountGender gender,
                                     Instant now) {
        return new Account(
                AccountId.newId(),
                idul,
                AccountName.of(name),
                EmailAddress.of(email),
                passwordHash,
                dateOfBirth,
                gender,
                AccountRole.USER,
                now
        );
    }

    public static Account createEmployee(String idul,
                                         String name,
                                         String email,
                                         PasswordHash passwordHash,
                                         DateOfBirth dateOfBirth,
                                         AccountGender gender,
                                         Instant now) {
        return new Account(
                AccountId.newId(),
                idul,
                AccountName.of(name),
                EmailAddress.of(email),
                passwordHash,
                dateOfBirth,
                gender,
                AccountRole.EMPLOYEE,
                now
        );
    }

    public AccountId id() {
        return id;
    }

    public String idul() {
        return idul;
    }

    public AccountName name() {
        return name;
    }

    public EmailAddress email() {
        return email;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public DateOfBirth dateOfBirth() {
        return dateOfBirth;
    }

    public AccountGender gender() {
        return gender;
    }

    public AccountRole role() {
        return role;
    }

    public boolean isEmployee() {
        return role == AccountRole.EMPLOYEE;
    }

    public Instant createdAt() {
        return createdAt;
    }

    private String requireIdul(String idul) {
        if (idul == null || idul.isBlank()) {
            throw new ValidationException("Account must have an idul");
        }
        return idul;
    }
}
