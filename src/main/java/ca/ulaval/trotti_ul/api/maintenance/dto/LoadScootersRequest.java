package ca.ulaval.trotti_ul.api.maintenance.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record LoadScootersRequest(
        @NotBlank String stationLocation,
        @NotEmpty List<@Min(1) Integer> slotNumbers
) {
}
