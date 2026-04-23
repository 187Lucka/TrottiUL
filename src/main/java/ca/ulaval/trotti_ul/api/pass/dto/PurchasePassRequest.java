package ca.ulaval.trotti_ul.api.pass.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PurchasePassRequest(
        @NotBlank String semesterCode,
        @Min(10) int dailyTripDurationMinutes,
        @NotBlank String billingMode
) {}
