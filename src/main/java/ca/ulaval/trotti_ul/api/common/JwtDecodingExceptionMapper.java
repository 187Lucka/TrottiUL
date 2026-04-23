package ca.ulaval.trotti_ul.api.common;

import java.util.Map;

import ca.ulaval.trotti_ul.infrastructure.security.jwt.JwtDecodingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JwtDecodingExceptionMapper implements ExceptionMapper<JwtDecodingException> {

    @Override
    public Response toResponse(JwtDecodingException exception) {
        Map<String, Object> body = Map.of(
                "error", exception.code(),
                "message", exception.getMessage()
        );

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(body)
                .build();
    }
}
