package ca.ulaval.trotti_ul.application.auth;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.auth.InvalidCredentialsException;
import ca.ulaval.trotti_ul.domain.security.PasswordHasher;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;
import ca.ulaval.trotti_ul.application.auth.TokenService;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LoginUseCase {

    private final AccountRepository accountRepository;
    private final TechnicianRepository technicianRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final Clock clock;

    public LoginUseCase(AccountRepository accountRepository,
                        TechnicianRepository technicianRepository,
                        PasswordHasher passwordHasher,
                        TokenService tokenService,
                        Clock clock) {
        this.accountRepository = accountRepository;
        this.technicianRepository = technicianRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
        this.clock = clock;
    }

    public AuthToken handle(LoginCommand cmd) {
        Account account = accountRepository.findByEmail(cmd.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(cmd.rawPassword(), account.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        Set<String> roles = new HashSet<>();
        roles.add("USER");

        if (account.isEmployee()) {
            roles.add("EMPLOYEE");
        }

        Optional<Technician> maybeTech = technicianRepository.findByAccountId(account.id());

        String technicianId = null;
        if (maybeTech.isPresent() && maybeTech.get().isActive()) {
            roles.add("TECHNICIAN");
            technicianId = maybeTech.get().id().toString();
        }

        Instant now = Instant.now(clock);
        Instant exp = now.plus(60, ChronoUnit.MINUTES);

        JwtClaims claims = new JwtClaims(
                account.id().toString(),
                Set.copyOf(roles),
                now,
                exp,
                technicianId
        );

        String token = tokenService.encode(claims);

        return new AuthToken(token, exp);
    }
}
