package ca.ulaval.trotti_ul.api.technician;

import ca.ulaval.trotti_ul.api.security.Public;
import ca.ulaval.trotti_ul.api.technician.dto.CreateTechnicianRequest;
import ca.ulaval.trotti_ul.api.technician.dto.CreateTechnicianResponse;
import ca.ulaval.trotti_ul.application.technician.CreateTechnicianCommand;
import ca.ulaval.trotti_ul.application.technician.CreateTechnicianUseCase;
import ca.ulaval.trotti_ul.application.technician.TechnicianCreationResult;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/technicians")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TechnicianResource {

    private final CreateTechnicianUseCase createTechnicianUseCase;

    @Inject
    public TechnicianResource(CreateTechnicianUseCase createTechnicianUseCase) {
        this.createTechnicianUseCase = createTechnicianUseCase;
    }

    @Public
    @POST
    public Response createTechnician(@Valid CreateTechnicianRequest request) {
        CreateTechnicianCommand cmd = new CreateTechnicianCommand(
                request.idul(),
                request.name(),
                request.email(),
                request.gender(),
                request.dateOfBirth(),
                request.password()
        );
        TechnicianCreationResult result = createTechnicianUseCase.handle(cmd);
        return Response.status(Response.Status.CREATED)
                .entity(CreateTechnicianResponse.from(result))
                .build();
    }
}
