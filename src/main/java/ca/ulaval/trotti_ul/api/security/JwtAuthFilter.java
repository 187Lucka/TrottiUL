package ca.ulaval.trotti_ul.api.security;

import ca.ulaval.trotti_ul.application.auth.DecodedToken;
import ca.ulaval.trotti_ul.application.auth.TokenService;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {

    private final TokenService tokenService;

    @Context
    private ResourceInfo resourceInfo;

    public JwtAuthFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (isPublic()) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        DecodedToken decoded = tokenService.decode(token);

        SecurityContext original = requestContext.getSecurityContext();

        JwtSecurityContext ctx = new JwtSecurityContext(
                decoded.subject(),
                decoded.roles(),
                decoded.technicianId(),
                original.isSecure()
        );

        requestContext.setSecurityContext(ctx);
    }

    private boolean isPublic() {
        if (resourceInfo.getResourceMethod().isAnnotationPresent(Public.class)) {
            return true;
        }
        return resourceInfo.getResourceClass().isAnnotationPresent(Public.class);
    }
}
