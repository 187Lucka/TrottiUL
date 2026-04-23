package ca.ulaval.trotti_ul.api.maintenance;

import java.util.List;

import ca.ulaval.trotti_ul.api.maintenance.dto.MaintenanceRequestRequest;
import ca.ulaval.trotti_ul.api.maintenance.dto.MaintenanceRequestResponse;
import ca.ulaval.trotti_ul.api.maintenance.dto.MaintenanceResponse;
import ca.ulaval.trotti_ul.application.maintenance.EndMaintenanceUseCase;
import ca.ulaval.trotti_ul.application.maintenance.GetMaintenanceRequestsUseCase;
import ca.ulaval.trotti_ul.application.maintenance.GetMaintenanceStatusUseCase;
import ca.ulaval.trotti_ul.application.maintenance.RequestMaintenanceUseCase;
import ca.ulaval.trotti_ul.application.maintenance.StartMaintenanceUseCase;
import ca.ulaval.trotti_ul.domain.maintenance.Maintenance;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.validation.Valid;

@Path("/maintenance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MaintenanceResource {

	private final RequestMaintenanceUseCase requestMaintenanceUseCase;
	private final GetMaintenanceRequestsUseCase getMaintenanceRequestsUseCase;
	private final StartMaintenanceUseCase startMaintenanceUseCase;
	private final EndMaintenanceUseCase endMaintenanceUseCase;
	private final GetMaintenanceStatusUseCase getMaintenanceStatusUseCase;

	@Inject
	public MaintenanceResource(RequestMaintenanceUseCase requestMaintenanceUseCase,
			GetMaintenanceRequestsUseCase getMaintenanceRequestsUseCase,
			StartMaintenanceUseCase startMaintenanceUseCase,
			EndMaintenanceUseCase endMaintenanceUseCase,
			GetMaintenanceStatusUseCase getMaintenanceStatusUseCase) {
		this.requestMaintenanceUseCase = requestMaintenanceUseCase;
		this.getMaintenanceRequestsUseCase = getMaintenanceRequestsUseCase;
		this.startMaintenanceUseCase = startMaintenanceUseCase;
		this.endMaintenanceUseCase = endMaintenanceUseCase;
		this.getMaintenanceStatusUseCase = getMaintenanceStatusUseCase;
	}

	@GET
	@RolesAllowed("TECHNICIAN")
	public Response getAllActiveMaintenance() {
		List<Maintenance> maintenances = getMaintenanceStatusUseCase.handleGetAll();
		List<MaintenanceResponse> responses = maintenances.stream()
				.map(MaintenanceResponse::from)
				.toList();
		return Response.ok(responses).build();
	}

	@GET
	@Path("/{stationLocation}")
	@RolesAllowed("TECHNICIAN")
	public Response getMaintenanceByStation(@PathParam("stationLocation") String stationLocation) {
		return getMaintenanceStatusUseCase.handleGetByStation(stationLocation)
				.map(m -> Response.ok(MaintenanceResponse.from(m)).build())
				.orElse(Response.status(Response.Status.NOT_FOUND)
						.entity("{\"error\":\"NO_ACTIVE_MAINTENANCE\",\"message\":\"No active maintenance for this station\"}")
						.build());
	}

    @POST
    @Path("/request")
    public Response requestMaintenance(@Context SecurityContext securityContext,
            @Valid MaintenanceRequestRequest request) {
		String accountId = securityContext.getUserPrincipal() != null
				? securityContext.getUserPrincipal().getName()
				: null;

		MaintenanceRequest maintenanceRequest = requestMaintenanceUseCase.handle(
				request.stationLocation(),
				accountId,
				request.reason());

		return Response.status(Response.Status.CREATED)
				.entity(MaintenanceRequestResponse.from(maintenanceRequest))
				.build();
	}

	@GET
	@Path("/requests")
	@RolesAllowed("TECHNICIAN")
	public Response getAllMaintenanceRequests() {
		List<MaintenanceRequestResponse> responses = getMaintenanceRequestsUseCase.handleGetAll().stream()
				.map(MaintenanceRequestResponse::from)
				.toList();

		return Response.ok(responses).build();
	}

    @GET
    @Path("/requests/pending")
    @RolesAllowed("TECHNICIAN")
    public Response getPendingMaintenanceRequests() {
        List<MaintenanceRequestResponse> responses = getMaintenanceRequestsUseCase.handleGetPending().stream()
				.map(MaintenanceRequestResponse::from)
				.toList();

        return Response.ok(responses).build();
    }

    @GET
    @Path("/requests/{requestId}")
    @RolesAllowed("TECHNICIAN")
    public Response getMaintenanceRequestById(@PathParam("requestId") String requestId) {
        MaintenanceRequest request = getMaintenanceRequestsUseCase.handleGetById(requestId);
        return Response.ok(MaintenanceRequestResponse.from(request)).build();
    }

	@POST
	@Path("/requests/{requestId}/start")
	@RolesAllowed("TECHNICIAN")
	public Response startMaintenanceFromRequest(@Context SecurityContext securityContext,
			@PathParam("requestId") String requestId) {
		String accountId = securityContext.getUserPrincipal().getName();

		Maintenance maintenance = startMaintenanceUseCase.handleFromRequest(accountId, requestId);

		return Response.status(Response.Status.CREATED)
				.entity(MaintenanceResponse.from(maintenance))
				.build();
	}

	@POST
	@Path("/requests/{requestId}/end")
	@RolesAllowed("TECHNICIAN")
	public Response endMaintenanceFromRequest(@Context SecurityContext securityContext,
			@PathParam("requestId") String requestId) {
		String accountId = securityContext.getUserPrincipal().getName();

		Maintenance maintenance = endMaintenanceUseCase.handleFromRequest(accountId, requestId);

		return Response.ok(MaintenanceResponse.from(maintenance)).build();
	}
}
