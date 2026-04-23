package ca.ulaval.trotti_ul.api.maintenance.dto;

import jakarta.validation.constraints.NotBlank;

public record MaintenanceRequestRequest(
        @NotBlank String stationLocation,
        @NotBlank String reason
) {
}
