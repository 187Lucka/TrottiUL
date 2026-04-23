package ca.ulaval.trotti_ul.application.technician;

public record CreateTechnicianCommand(
        String idul,
        String name,
        String email,
        String gender,
        String dateOfBirth,
        String rawPassword
) {}
