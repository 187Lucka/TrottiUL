package ca.ulaval.trotti_ul.api.maintenance.dto;

import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequest;

public record MaintenanceRequestResponse(
        String id,
        String stationLocation,
        String reason,
        String status,
        String createdAt
) {
    public static MaintenanceRequestResponse from(MaintenanceRequest request) {
        return new MaintenanceRequestResponse(
                request.id().toString(),
                request.stationLocation().value(),
                request.reason(),
                request.status().name(),
                request.createdAt().toString()
        );
    }
}
