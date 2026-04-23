package ca.ulaval.trotti_ul.api.auth;

import java.time.LocalDate;

import ca.ulaval.trotti_ul.api.auth.dto.LoginRequest;
import ca.ulaval.trotti_ul.api.auth.dto.LoginResponse;
import ca.ulaval.trotti_ul.api.auth.dto.SignUpRequest;
import ca.ulaval.trotti_ul.api.auth.dto.SignUpResponse;
import ca.ulaval.trotti_ul.application.account.CreateAccountCommand;
import ca.ulaval.trotti_ul.application.account.CreateAccountUseCase;
import ca.ulaval.trotti_ul.application.auth.AuthToken;
import ca.ulaval.trotti_ul.application.auth.LoginCommand;
import ca.ulaval.trotti_ul.application.auth.LoginUseCase;
import ca.ulaval.trotti_ul.api.security.Public;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Public
public class AuthResource {

    private final CreateAccountUseCase createAccountUseCase;
    private final LoginUseCase loginUseCase;

    @Inject
    public AuthResource(CreateAccountUseCase createAccountUseCase,
            LoginUseCase loginUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.loginUseCase = loginUseCase;
    }

    @POST
    @Path("/signup")
    public Response signUp(@Valid SignUpRequest request) {
        var cmd = new CreateAccountCommand(
                request.idul(),
                request.name(),
                request.email(),
                request.password(),
                request.gender(),
                request.dateOfBirth());
        var account = createAccountUseCase.handle(cmd);
        int age = account.dateOfBirth().ageOn(LocalDate.now());
        return Response.status(Response.Status.CREATED)
                .entity(new SignUpResponse(account.idul(), account.name().value(), account.email().value(), account.gender().asString(),
                        account.dateOfBirth().value().toString(), age))
                .build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        var cmd = new LoginCommand(request.email(), request.password());
        AuthToken token = loginUseCase.handle(cmd);
        return Response.ok(new LoginResponse(token.accessToken(), token.expiresAt()))
                .build();
    }
}
