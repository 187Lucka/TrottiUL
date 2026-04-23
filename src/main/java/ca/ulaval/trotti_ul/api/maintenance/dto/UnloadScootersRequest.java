package ca.ulaval.trotti_ul.api.maintenance.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record UnloadScootersRequest(
        @NotBlank String destinationStationLocation,
        @NotEmpty List<@Valid ScooterPlacement> placements
) {
    public record ScooterPlacement(
            @NotBlank String scooterId,
            @Min(1) int slotNumber
    ) {}
}
