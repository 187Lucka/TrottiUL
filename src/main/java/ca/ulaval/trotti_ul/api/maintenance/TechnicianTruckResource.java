package ca.ulaval.trotti_ul.api.maintenance;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ca.ulaval.trotti_ul.api.maintenance.dto.LoadScootersRequest;
import ca.ulaval.trotti_ul.api.maintenance.dto.TransferredScooterResponse;
import ca.ulaval.trotti_ul.api.maintenance.dto.TruckContentsResponse;
import ca.ulaval.trotti_ul.api.maintenance.dto.UnloadScootersRequest;
import ca.ulaval.trotti_ul.application.maintenance.GetTruckContentsUseCase;
import ca.ulaval.trotti_ul.application.maintenance.LoadScootersToTruckUseCase;
import ca.ulaval.trotti_ul.application.maintenance.UnloadScootersFromTruckUseCase;
import ca.ulaval.trotti_ul.domain.maintenance.TransferredScooter;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/technician/truck")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TechnicianTruckResource {

    private final LoadScootersToTruckUseCase loadScootersToTruckUseCase;
    private final UnloadScootersFromTruckUseCase unloadScootersFromTruckUseCase;
    private final GetTruckContentsUseCase getTruckContentsUseCase;

    @Inject
    public TechnicianTruckResource(LoadScootersToTruckUseCase loadScootersToTruckUseCase,
                                   UnloadScootersFromTruckUseCase unloadScootersFromTruckUseCase,
                                   GetTruckContentsUseCase getTruckContentsUseCase) {
        this.loadScootersToTruckUseCase = loadScootersToTruckUseCase;
        this.unloadScootersFromTruckUseCase = unloadScootersFromTruckUseCase;
        this.getTruckContentsUseCase = getTruckContentsUseCase;
    }

    @POST
    @Path("/load")
    @RolesAllowed("TECHNICIAN")
    public Response loadScooters(@Context SecurityContext securityContext,
                                 @Valid LoadScootersRequest request) {
        String accountId = securityContext.getUserPrincipal().getName();

        List<TransferredScooter> loaded = loadScootersToTruckUseCase.handle(
                accountId,
                request.stationLocation(),
                request.slotNumbers()
        );

        List<TransferredScooterResponse> responses = loaded.stream()
                .map(TransferredScooterResponse::from)
                .toList();

        return Response.ok(responses).build();
    }

    @POST
    @Path("/unload")
    @RolesAllowed("TECHNICIAN")
    public Response unloadScooters(@Context SecurityContext securityContext,
                                   @Valid UnloadScootersRequest request) {
        String accountId = securityContext.getUserPrincipal().getName();

        Map<String, Integer> placements = request.placements().stream()
                .collect(Collectors.toMap(
                        UnloadScootersRequest.ScooterPlacement::scooterId,
                        UnloadScootersRequest.ScooterPlacement::slotNumber
                ));

        List<TransferredScooter> unloaded = unloadScootersFromTruckUseCase.handle(
                accountId,
                request.destinationStationLocation(),
                placements
        );

        List<TransferredScooterResponse> responses = unloaded.stream()
                .map(TransferredScooterResponse::from)
                .toList();

        return Response.ok(responses).build();
    }

    @GET
    @RolesAllowed("TECHNICIAN")
    public Response getTruckContents(@Context SecurityContext securityContext) {
        String accountId = securityContext.getUserPrincipal().getName();

        List<TransferredScooter> scooters = getTruckContentsUseCase.handle(accountId);

        return Response.ok(TruckContentsResponse.from(scooters)).build();
    }
}
