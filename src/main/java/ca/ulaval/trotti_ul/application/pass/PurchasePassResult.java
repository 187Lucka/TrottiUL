package ca.ulaval.trotti_ul.application.pass;

import ca.ulaval.trotti_ul.domain.billing.Invoice;
import ca.ulaval.trotti_ul.domain.pass.Pass;

public record PurchasePassResult(Pass pass, Invoice invoice) {}
