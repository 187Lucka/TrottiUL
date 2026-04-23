package ca.ulaval.trotti_ul.api.auth.dto;

public record SignUpResponse(
        String idul,
        String name,
        String email,
        String gender,
        String dateOfBirth,
        int age
) {}
