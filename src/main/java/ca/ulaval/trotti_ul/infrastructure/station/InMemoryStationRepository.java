package ca.ulaval.trotti_ul.infrastructure.station;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.ulaval.trotti_ul.domain.station.Station;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.StationSnapshot;
import ca.ulaval.trotti_ul.domain.station.StationStatus;
import ca.ulaval.trotti_ul.domain.common.TechnicalException;

public class InMemoryStationRepository implements StationRepository {

    private record StationInternal(String location, String name, int capacity) { }

    private final Map<StationLocation, StationInternal> stations = new ConcurrentHashMap<>();
    private final Map<StationLocation, StationStatus> stationStatuses = new ConcurrentHashMap<>();

    public InMemoryStationRepository() {
        loadStations();
    }

    @Override
    public List<Station> findAll() {
        return stations.values().stream()
                .map(this::toStation)
                .toList();
    }

    @Override
    public Optional<Station> findByLocation(StationLocation location) {
        StationInternal st = stations.get(location);
        if (st == null) return Optional.empty();
        return Optional.of(toStation(st));
    }

    @Override
    public Optional<Station> findByLocationName(String location) {
        if (location == null || location.isBlank()) return Optional.empty();
        return Optional.ofNullable(location)
                .filter(l -> !l.isBlank())
                .map(StationLocation::fromString)
                .flatMap(this::findByLocation);
    }

    public Optional<StationSnapshot> findSnapshotByLocation(StationLocation location) {
        return findByLocation(location).map(Station::snapshot);
    }

    public Optional<StationSnapshot> findSnapshotByLocationName(String location) {
        return findByLocationName(location).map(Station::snapshot);
    }

    private Station toStation(StationInternal st) {
        StationLocation loc = StationLocation.fromString(st.location());
        return new Station(loc, st.name(), st.capacity());
    }

    @Override
    public StationStatus getStatus(StationLocation location) {
        return stationStatuses.getOrDefault(location, StationStatus.OPERATIONAL);
    }

    @Override
    public void updateStatus(StationLocation location, StationStatus status) {
        stationStatuses.put(location, status);
    }

    @Override
    public boolean isUnderMaintenance(StationLocation location) {
        return getStatus(location) == StationStatus.UNDER_MAINTENANCE;
    }

    private void loadStations() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("stations.json")) {
            List<Map<String, Object>> raw = mapper.readValue(in, new TypeReference<>() {});
            for (Map<String, Object> station : raw) {
                String locStr = (String) station.get("location");
                StationLocation location = StationLocation.fromString(locStr);
                String name = (String) station.get("name");
                int capacity = (Integer) station.get("capacity");
                stations.put(location, new StationInternal(locStr, name, capacity));
                stationStatuses.put(location, StationStatus.OPERATIONAL);
            }
        } catch (IOException e) {
            throw new TechnicalException("Failed to load stations", e);
        }
    }
}
