package ca.ulaval.trotti_ul.api.maintenance.dto;

import ca.ulaval.trotti_ul.domain.maintenance.TransferredScooter;

public record TransferredScooterResponse(
        String scooterId,
        String originStation,
        int originSlotNumber,
        int energyPercent
) {
    public static TransferredScooterResponse from(TransferredScooter scooter) {
        return new TransferredScooterResponse(
                scooter.scooterId(),
                scooter.originStation().value(),
                scooter.originSlotNumber(),
                scooter.energyPercent()
        );
    }
}
