package ca.ulaval.trotti_ul.api.ride.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StartRideRequest(
        @NotBlank String code,
        @NotBlank String stationLocation,
        @Min(1) int slotNumber
) {}
