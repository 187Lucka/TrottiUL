package ca.ulaval.trotti_ul.domain.station;

import java.util.List;
import java.util.Optional;

public interface StationRepository {
    List<Station> findAll();
    Optional<Station> findByLocation(StationLocation location);
    Optional<Station> findByLocationName(String location);

    StationStatus getStatus(StationLocation location);
    void updateStatus(StationLocation location, StationStatus status);
    boolean isUnderMaintenance(StationLocation location);
}
