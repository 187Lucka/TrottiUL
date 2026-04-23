package ca.ulaval.trotti_ul.api.account;

import java.math.BigDecimal;
import java.util.List;

import ca.ulaval.trotti_ul.api.account.dto.InvoiceLineResponse;
import ca.ulaval.trotti_ul.api.account.dto.InvoiceResponse;
import ca.ulaval.trotti_ul.application.billing.GetAccountInvoicesUseCase;
import ca.ulaval.trotti_ul.domain.billing.Invoice;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/accounts/invoices")
@Produces(MediaType.APPLICATION_JSON)
public class AccountInvoiceResource {

    private final GetAccountInvoicesUseCase getAccountInvoicesUseCase;

    @Inject
    public AccountInvoiceResource(GetAccountInvoicesUseCase getAccountInvoicesUseCase) {
        this.getAccountInvoicesUseCase = getAccountInvoicesUseCase;
    }

    @GET
    public Response getInvoices(@Context SecurityContext securityContext) {
        String accountId = securityContext.getUserPrincipal().getName();
        List<InvoiceResponse> invoices = getAccountInvoicesUseCase.handle(accountId).stream()
                .map(AccountInvoiceResource::toResponse)
                .toList();
        return Response.ok(invoices).build();
    }

    private static InvoiceResponse toResponse(Invoice invoice) {
        List<InvoiceLineResponse> lines = invoice.lines().stream()
                .map(l -> new InvoiceLineResponse(l.description(), toDollars(l.amountInCents())))
                .toList();
        return new InvoiceResponse(
                invoice.id().value().toString(),
                invoice.semesterCode() != null ? invoice.semesterCode().value() : null,
                invoice.transactionId(),
                invoice.createdAt(),
                invoice.status().name(),
                toDollars(invoice.totalInCents()),
                lines
        );
    }

    private static BigDecimal toDollars(int cents) {
        return BigDecimal.valueOf(cents).movePointLeft(2);
    }
}
