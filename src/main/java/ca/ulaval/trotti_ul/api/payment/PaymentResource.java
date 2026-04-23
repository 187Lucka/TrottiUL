package ca.ulaval.trotti_ul.api.payment;

import ca.ulaval.trotti_ul.api.payment.dto.AddCreditCardRequest;
import ca.ulaval.trotti_ul.api.payment.dto.CreditCardResponse;
import ca.ulaval.trotti_ul.application.payment.AddCreditCardCommand;
import ca.ulaval.trotti_ul.application.payment.AddCreditCardUseCase;
import ca.ulaval.trotti_ul.domain.payment.CreditCard;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

@Path("/accounts/payment-method")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    private final AddCreditCardUseCase addCreditCardUseCase;

    @Inject
    public PaymentResource(AddCreditCardUseCase addCreditCardUseCase) {
        this.addCreditCardUseCase = addCreditCardUseCase;
    }

    @POST
    public Response addCreditCard(@Context SecurityContext securityContext,
                                  @Valid AddCreditCardRequest request) {
        String accountId = securityContext.getUserPrincipal().getName();

        AddCreditCardCommand command = new AddCreditCardCommand(
                accountId,
                request.cardNumber(),
                request.expiry(),
                request.cvv()
        );

        CreditCard creditCard = addCreditCardUseCase.handle(command);

        return Response.status(Response.Status.CREATED)
                .entity(CreditCardResponse.from(creditCard))
                .build();
    }
}
