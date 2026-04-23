package ca.ulaval.trotti_ul.application.account;

public record CreateAccountCommand(
        String idul,
        String name,
        String email,
        String rawPassword,
        String gender,
        String dateOfBirth
) {}
