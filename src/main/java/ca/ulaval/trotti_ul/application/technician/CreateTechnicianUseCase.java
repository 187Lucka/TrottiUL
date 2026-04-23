package ca.ulaval.trotti_ul.application.technician;

import java.time.Clock;
import java.time.Instant;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountAlreadyExistsException;
import ca.ulaval.trotti_ul.domain.account.AccountGender;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.account.DateOfBirth;
import ca.ulaval.trotti_ul.domain.account.PasswordHash;
import ca.ulaval.trotti_ul.domain.employee.EmployeeCatalog;
import ca.ulaval.trotti_ul.domain.security.PasswordHasher;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianId;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;
import ca.ulaval.trotti_ul.domain.technician.TechnicianMustBeUlEmployeeException;
import ca.ulaval.trotti_ul.domain.technician.TechnicianAlreadyExistsException;

public class CreateTechnicianUseCase {

    private final AccountRepository accountRepository;
    private final TechnicianRepository technicianRepository;
    private final EmployeeCatalog employeeCatalog;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public CreateTechnicianUseCase(AccountRepository accountRepository,
                                   TechnicianRepository technicianRepository,
                                   EmployeeCatalog employeeCatalog,
                                   PasswordHasher passwordHasher,
                                   Clock clock) {
        this.accountRepository = accountRepository;
        this.technicianRepository = technicianRepository;
        this.employeeCatalog = employeeCatalog;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    public TechnicianCreationResult handle(CreateTechnicianCommand cmd) {
        if (!employeeCatalog.existsByIdul(cmd.idul())) {
            throw new TechnicianMustBeUlEmployeeException();
        }

        Account account = accountRepository.findByIdul(cmd.idul())
                .orElseGet(() -> createEmployeeAccount(cmd));

        technicianRepository.findByAccountId(account.id())
                .ifPresent(t -> {
                    throw new TechnicianAlreadyExistsException();
                });

        Technician technician = Technician.create(account);
        technicianRepository.save(technician);

        return new TechnicianCreationResult(technician.id(), account);
    }

    private Account createEmployeeAccount(CreateTechnicianCommand cmd) {
        Instant now = Instant.now(clock);

        accountRepository.findByEmail(cmd.email()).ifPresent(a -> {
            throw AccountAlreadyExistsException.forEmail(cmd.email());
        });

        PasswordHash.validateRawPassword(cmd.rawPassword());
        PasswordHash hash = passwordHasher.hash(cmd.rawPassword());

        AccountGender gender = AccountGender.from(cmd.gender());

        Account acc = Account.createEmployee(
                cmd.idul(),
                cmd.name(),
                cmd.email(),
                hash,
                DateOfBirth.from(cmd.dateOfBirth()),
                gender,
                now);
        accountRepository.save(acc);
        return acc;
    }
}
