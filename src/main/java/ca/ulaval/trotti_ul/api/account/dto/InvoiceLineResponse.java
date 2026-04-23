package ca.ulaval.trotti_ul.api.account.dto;

import java.math.BigDecimal;

public record InvoiceLineResponse(
        String description,
        BigDecimal amount
) {}
