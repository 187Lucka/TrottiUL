package ca.ulaval.trotti_ul.api.maintenance.dto;

import ca.ulaval.trotti_ul.domain.maintenance.Maintenance;

public record MaintenanceResponse(
        String id,
        String stationLocation,
        String technicianId,
        String startedAt,
        String completedAt,
        boolean active
) {
    public static MaintenanceResponse from(Maintenance maintenance) {
        return new MaintenanceResponse(
                maintenance.id().toString(),
                maintenance.stationLocation().value(),
                maintenance.technicianId().toString(),
                maintenance.startedAt().toString(),
                maintenance.completedAt().map(Object::toString).orElse(null),
                maintenance.isActive()
        );
    }
}
