package ca.ulaval.trotti_ul.application.account;

import java.time.Clock;
import java.time.Instant;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountAlreadyExistsException;
import ca.ulaval.trotti_ul.domain.account.AccountGender;
import ca.ulaval.trotti_ul.domain.account.AccountName;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.account.DateOfBirth;
import ca.ulaval.trotti_ul.domain.account.PasswordHash;
import ca.ulaval.trotti_ul.domain.employee.EmployeeCatalog;
import ca.ulaval.trotti_ul.domain.security.PasswordHasher;

public class CreateAccountUseCase {

    private final AccountRepository accountRepository;
    private final EmployeeCatalog employeeCatalog;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public CreateAccountUseCase(AccountRepository accountRepository,
                                EmployeeCatalog employeeCatalog,
                                PasswordHasher passwordHasher,
                                Clock clock) {
        this.accountRepository = accountRepository;
        this.employeeCatalog = employeeCatalog;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    public Account handle(CreateAccountCommand cmd) {
        Instant now = Instant.now(clock);

        PasswordHash.validateRawPassword(cmd.rawPassword());
        PasswordHash hash = passwordHasher.hash(cmd.rawPassword());
        DateOfBirth dob = DateOfBirth.from(cmd.dateOfBirth());
        AccountGender gender = AccountGender.from(cmd.gender());
        AccountName name = AccountName.of(cmd.name());

        accountRepository.findByEmail(cmd.email()).ifPresent(a -> {
            throw AccountAlreadyExistsException.forEmail(cmd.email());
        });
        if (cmd.idul() != null && !cmd.idul().isBlank()) {
            accountRepository.findByIdul(cmd.idul()).ifPresent(a -> {
                throw AccountAlreadyExistsException.forIdul(cmd.idul());
            });
        }

        boolean isEmployee = cmd.idul() != null
                && employeeCatalog.existsByIdul(cmd.idul());

        Account account;

        if (isEmployee) {
            account = Account.createEmployee(cmd.idul(), name.value(), cmd.email(), hash, dob, gender, now);
        } else {
            account = Account.createUser(cmd.idul(), name.value(), cmd.email(), hash, dob, gender, now);
        }

        accountRepository.save(account);
        return account;
    }
}
