package ca.ulaval.trotti_ul.domain.ride;

import java.util.Objects;
import java.util.UUID;

public record RideId(UUID value) {
    public static RideId newId() {
        return new RideId(UUID.randomUUID());
    }
}
