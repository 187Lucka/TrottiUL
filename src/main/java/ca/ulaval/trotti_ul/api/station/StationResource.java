package ca.ulaval.trotti_ul.api.station;

import java.util.List;

import ca.ulaval.trotti_ul.api.station.dto.StationResponse;
import ca.ulaval.trotti_ul.api.station.dto.StationSummaryResponse;
import ca.ulaval.trotti_ul.application.station.GetStationDetailsUseCase;
import ca.ulaval.trotti_ul.application.station.GetStationsUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/stations")
@Produces(MediaType.APPLICATION_JSON)
public class StationResource {

    private final GetStationsUseCase getStationsUseCase;
    private final GetStationDetailsUseCase getStationDetailsUseCase;

    @Inject
    public StationResource(GetStationsUseCase getStationsUseCase,
                           GetStationDetailsUseCase getStationDetailsUseCase) {
        this.getStationsUseCase = getStationsUseCase;
        this.getStationDetailsUseCase = getStationDetailsUseCase;
    }

    @GET
    public Response getStations() {
        List<StationSummaryResponse> stations = getStationsUseCase.handle().stream()
                .map(dto -> StationSummaryResponse.from(dto.snapshot(), dto.scootersCount()))
                .toList();
        return Response.ok(stations).build();
    }

    @GET
    @Path("/{location}/scooters")
    public Response getScooters(@PathParam("location") String location) {
        if (location == null || location.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"INVALID_LOCATION\",\"message\":\"location path param is required\"}")
                    .build();
        }
        return getStationDetailsUseCase.handle(location)
                .map(dto -> dto.scooters().stream()
                        .map(s -> new StationResponse.ScooterSlotResponse(s.slotNumber(), s.energyPercent(), s.occupied()))
                        .toList())
                .<Response>map(body -> Response.ok(body).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"UNKNOWN_STATION\",\"message\":\"Station not found\"}")
                        .build());
    }
}
