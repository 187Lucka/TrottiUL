package ca.ulaval.trotti_ul.api.semester;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import ca.ulaval.trotti_ul.api.semester.dto.SemesterResponse;
import ca.ulaval.trotti_ul.domain.semester.SemesterCatalog;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/semesters")
@Produces(MediaType.APPLICATION_JSON)

public class SemesterResource {

    private final SemesterCatalog semesterCatalog;
    private final Clock clock;

    @Inject
    public SemesterResource(SemesterCatalog semesterCatalog, Clock clock) {
        this.semesterCatalog = semesterCatalog;
        this.clock = clock;
    }

    @GET
    public Response getAllSemesters() {
        LocalDate today = LocalDate.now(clock);
        List<SemesterResponse> semesters = semesterCatalog.findPurchasableSemesters(today).stream()
                .map(s -> SemesterResponse.from(s, today))
                .toList();
        return Response.ok(semesters).build();
    }

    @GET
    @Path("/current")
    public Response getCurrentSemester() {
        LocalDate today = LocalDate.now(clock);
        return semesterCatalog.findCurrentSemester(today)
                .map(s -> Response.ok(SemesterResponse.from(s, today)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"NO_ACTIVE_SEMESTER\", \"message\": \"No active semester at this time\"}")
                        .build());
    }
}
