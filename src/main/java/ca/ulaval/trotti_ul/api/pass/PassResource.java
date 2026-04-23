package ca.ulaval.trotti_ul.api.pass;

import java.util.List;
import java.time.LocalDate;

import ca.ulaval.trotti_ul.api.pass.dto.PassResponse;
import ca.ulaval.trotti_ul.api.pass.dto.PurchasePassRequest;
import ca.ulaval.trotti_ul.api.pass.dto.PurchasePassResponse;
import ca.ulaval.trotti_ul.application.pass.GetAccountPassesUseCase;
import ca.ulaval.trotti_ul.application.pass.GetValidPassUseCase;
import ca.ulaval.trotti_ul.application.pass.PurchasePassCommand;
import ca.ulaval.trotti_ul.application.pass.PurchasePassResult;
import ca.ulaval.trotti_ul.application.pass.PurchasePassUseCase;
import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.account.AccountRole;
import ca.ulaval.trotti_ul.domain.pass.AccessPlanService;
import ca.ulaval.trotti_ul.domain.pass.EffectivePass;
import ca.ulaval.trotti_ul.domain.pass.Pass;
import ca.ulaval.trotti_ul.domain.account.AccountNotFoundException;
import ca.ulaval.trotti_ul.domain.pass.AccessPlanNotFoundException;
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

@Path("/accounts/passes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PassResource {

    private final PurchasePassUseCase purchasePassUseCase;
    private final GetAccountPassesUseCase getAccountPassesUseCase;
    private final GetValidPassUseCase getValidPassUseCase;
    private final AccessPlanService accessPlanService;
    private final AccountRepository accountRepository;

    @Inject
    public PassResource(PurchasePassUseCase purchasePassUseCase,
                        GetAccountPassesUseCase getAccountPassesUseCase,
                        GetValidPassUseCase getValidPassUseCase,
                        AccessPlanService accessPlanService,
                        AccountRepository accountRepository) {
        this.purchasePassUseCase = purchasePassUseCase;
        this.getAccountPassesUseCase = getAccountPassesUseCase;
        this.getValidPassUseCase = getValidPassUseCase;
        this.accessPlanService = accessPlanService;
        this.accountRepository = accountRepository;
    }

    @POST
    public Response purchasePass(@Context SecurityContext securityContext,
                                 @Valid PurchasePassRequest request) {
        String accountId = securityContext.getUserPrincipal().getName();

        PurchasePassCommand command = new PurchasePassCommand(
                accountId,
                request.semesterCode(),
                request.dailyTripDurationMinutes(),
                request.billingMode()
        );

        PurchasePassResult result = purchasePassUseCase.handle(command);

        return Response.status(Response.Status.CREATED)
                .entity(PurchasePassResponse.from(result.pass()))
                .build();
    }

    @GET
    public Response getAccountPasses(@Context SecurityContext securityContext) {
        String accountId = securityContext.getUserPrincipal().getName();

        Account account = accountRepository.findById(AccountId.fromString(accountId))
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        if (account.role() == AccountRole.EMPLOYEE) {
            EffectivePass plan = accessPlanService.getEffectivePassFor(account, LocalDate.now())
                    .orElseThrow(AccessPlanNotFoundException::new);
            PassResponse response = PassResponse.fromEffective("EMPLOYEE_ACCESS", plan);
            return Response.ok(List.of(response)).build();
        } else {
            List<Pass> passes = getAccountPassesUseCase.handle(accountId);
            List<PassResponse> response = passes.stream()
                    .map(PassResponse::from)
                    .toList();
            return Response.ok(response).build();
        }
    }

    @GET
    @Path("/valid")
    public Response getValidPass(@Context SecurityContext securityContext) {
        String accountId = securityContext.getUserPrincipal().getName();

        Account account = accountRepository.findById(AccountId.fromString(accountId))
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        if (account.role() == AccountRole.EMPLOYEE) {
            return accessPlanService.getEffectivePassFor(account, LocalDate.now())
                    .map(plan -> Response.ok(PassResponse.fromEffective("EMPLOYEE_ACCESS", plan)).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity("{\"error\": \"NO_VALID_PASS\", \"message\": \"No valid pass for the current semester\"}")
                            .build());
        }

        return getValidPassUseCase.handle(accountId)
                .map(pass -> Response.ok(PassResponse.from(pass)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"NO_VALID_PASS\", \"message\": \"No valid pass for the current semester\"}")
                        .build());
    }
}
