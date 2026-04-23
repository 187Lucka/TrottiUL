package ca.ulaval.trotti_ul.application.pass;

public record PurchasePassCommand(
        String accountId,
        String semesterCode,
        int dailyTripDurationMinutes,
        String billingMode
) {}
