package ca.ulaval.trotti_ul.api.account;

import ca.ulaval.trotti_ul.api.account.dto.BalanceResponse;
import ca.ulaval.trotti_ul.application.billing.GetAccountBalanceUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/accounts/balance")
@Produces(MediaType.APPLICATION_JSON)
public class AccountBalanceResource {

    private final GetAccountBalanceUseCase getAccountBalanceUseCase;

    @Inject
    public AccountBalanceResource(GetAccountBalanceUseCase getAccountBalanceUseCase) {
        this.getAccountBalanceUseCase = getAccountBalanceUseCase;
    }

    @GET
    public Response getBalance(@Context SecurityContext securityContext) {
        String accountId = securityContext.getUserPrincipal().getName();
        var result = getAccountBalanceUseCase.handle(accountId);
        return Response.ok(new BalanceResponse(result.balance())).build();
    }
}
