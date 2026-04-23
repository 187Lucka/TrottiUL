package ca.ulaval.trotti_ul.domain.security;

import ca.ulaval.trotti_ul.domain.account.PasswordHash;

public interface PasswordHasher {

    PasswordHash hash(String rawPassword);

    boolean matches(String rawPassword, PasswordHash hash);
}