package ca.ulaval.trotti_ul.domain.ride;

import ca.ulaval.trotti_ul.domain.station.StationLocation;

public record RideWithPolicyResult(Ride ride, int extraChargeCents, int remainingEnergy, StationLocation endStation) { }
