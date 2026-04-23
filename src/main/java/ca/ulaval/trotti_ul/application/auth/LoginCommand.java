package ca.ulaval.trotti_ul.application.auth;

public record LoginCommand(
        String email,
        String rawPassword
) {}