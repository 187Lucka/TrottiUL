package ca.ulaval.trotti_ul.api.ride.dto;

import java.time.Instant;
import java.math.BigDecimal;

import ca.ulaval.trotti_ul.domain.ride.Ride;

public record RideResponse(
        String id,
        String startStation,
        String endStation,
        Instant startTime,
        Instant endTime,
        long durationMinutes,
        long durationSeconds,
        BigDecimal extraChargeDollars
) {
    public static RideResponse from(Ride ride) {
        return new RideResponse(
                ride.id().value().toString(),
                ride.startStation().value(),
                ride.endStation() != null ? ride.endStation().value() : null,
                ride.startTime(),
                ride.endTime(),
                ride.durationMinutes(),
                ride.durationSeconds(),
                BigDecimal.valueOf(ride.extraChargeCents()).movePointLeft(2)
        );
    }
}
