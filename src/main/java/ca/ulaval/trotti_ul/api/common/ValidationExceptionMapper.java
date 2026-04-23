package ca.ulaval.trotti_ul.api.common;

import java.util.Map;

import ca.ulaval.trotti_ul.domain.common.ValidationException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException exception) {
        Map<String, Object> body = Map.of(
                "error", exception.code(),
                "message", exception.getMessage()
        );
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(body)
                .build();
    }
}
