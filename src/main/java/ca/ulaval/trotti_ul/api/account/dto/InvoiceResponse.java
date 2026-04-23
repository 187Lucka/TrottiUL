package ca.ulaval.trotti_ul.api.account.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record InvoiceResponse(
        String id,
        String semesterCode,
        String transactionId,
        Instant createdAt,
        String status,
        BigDecimal total,
        List<InvoiceLineResponse> lines
) {}
