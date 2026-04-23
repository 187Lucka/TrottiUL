package ca.ulaval.trotti_ul.api.technician.dto;

public record CreateTechnicianResponse(
        String idul,
        String name,
        String email,
        String gender,
        String dateOfBirth
) {
    public static CreateTechnicianResponse from(CreateTechnicianRequest req) {
        return new CreateTechnicianResponse(
                req.idul(),
                req.name(),
                req.email(),
                req.gender(),
                req.dateOfBirth()
        );
    }

    public static CreateTechnicianResponse from(ca.ulaval.trotti_ul.application.technician.TechnicianCreationResult result) {
        var account = result.account();
        return new CreateTechnicianResponse(
                account.idul(),
                account.name().value(),
                account.email().value(),
                account.gender().asString(),
                account.dateOfBirth().value().toString()
        );
    }
}
