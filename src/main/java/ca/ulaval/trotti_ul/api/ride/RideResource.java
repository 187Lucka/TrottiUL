package ca.ulaval.trotti_ul.api.ride;

import ca.ulaval.trotti_ul.api.ride.dto.EndRideRequest;
import ca.ulaval.trotti_ul.api.ride.dto.GenerateCodeResponse;
import ca.ulaval.trotti_ul.api.ride.dto.RideResponse;
import ca.ulaval.trotti_ul.api.ride.dto.StartRideRequest;
import ca.ulaval.trotti_ul.application.ride.EndRideUseCase;
import ca.ulaval.trotti_ul.application.ride.GenerateCodeUseCase;
import ca.ulaval.trotti_ul.application.ride.GetRideHistoryUseCase;
import ca.ulaval.trotti_ul.application.ride.StartRideUseCase;
import ca.ulaval.trotti_ul.domain.ride.GenerateCode;
import ca.ulaval.trotti_ul.domain.ride.Ride;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.validation.Valid;

@Path("/rides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RideResource {

    private final GenerateCodeUseCase generateCodeUseCase;
    private final StartRideUseCase startRideUseCase;
    private final EndRideUseCase endRideUseCase;
    private final GetRideHistoryUseCase getRideHistoryUseCase;

    @Inject
    public RideResource(GenerateCodeUseCase generateCodeUseCase,
                        StartRideUseCase startRideUseCase,
                        EndRideUseCase endRideUseCase,
                        GetRideHistoryUseCase getRideHistoryUseCase) {
        this.generateCodeUseCase = generateCodeUseCase;
        this.startRideUseCase = startRideUseCase;
        this.endRideUseCase = endRideUseCase;
        this.getRideHistoryUseCase = getRideHistoryUseCase;
    }

    @POST
    @Path("/generate-code")
    public Response generateCode(@Context SecurityContext securityContext) {
        String accountId = securityContext.getUserPrincipal().getName();
        GenerateCode code = generateCodeUseCase.handle(accountId);
        return Response.status(Response.Status.CREATED)
                .entity(GenerateCodeResponse.from(code))
                .build();
    }

    @POST
    @Path("/start")
    public Response startRide(@Context SecurityContext securityContext, @Valid StartRideRequest request) {
        String accountId = securityContext.getUserPrincipal().getName();
        startRideUseCase.handle(
                accountId,
                request.code(),
                request.stationLocation(),
                request.slotNumber()
        );
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/end")
    public Response endRide(@Context SecurityContext securityContext, @Valid EndRideRequest request) {
        String accountId = securityContext.getUserPrincipal().getName();
        Ride ride = endRideUseCase.handle(
                accountId,
                request.stationLocation(),
                request.slotNumber()
        );
        return Response.ok(RideResponse.from(ride)).build();
    }

    @GET
    @Path("/history")
    public Response history(@Context SecurityContext securityContext) {
        String accountId = securityContext.getUserPrincipal().getName();
        var rides = getRideHistoryUseCase.handle(accountId).stream()
                .map(RideResponse::from)
                .toList();
        return Response.ok(rides).build();
    }
}
