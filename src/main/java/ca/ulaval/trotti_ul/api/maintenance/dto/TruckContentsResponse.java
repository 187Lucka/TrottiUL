package ca.ulaval.trotti_ul.api.maintenance.dto;

import java.util.List;

import ca.ulaval.trotti_ul.domain.maintenance.TransferredScooter;

public record TruckContentsResponse(
        int scooterCount,
        List<TransferredScooterResponse> scooters
) {
    public static TruckContentsResponse from(List<TransferredScooter> scooters) {
        return new TruckContentsResponse(
                scooters.size(),
                scooters.stream().map(TransferredScooterResponse::from).toList()
        );
    }
}
