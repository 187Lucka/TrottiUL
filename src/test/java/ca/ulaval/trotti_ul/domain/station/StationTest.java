package ca.ulaval.trotti_ul.domain.station;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class StationTest {

    @Test
    void shouldCreateStationWithValidParameters() {
        // Given
        StationLocation location = new StationLocation("46.78,-71.29");
        String name = "Station Test";
        int capacity = 10;

        // When
        Station station = new Station(location, name, capacity);

        // Then
        assertThat(station).isNotNull();
        assertThat(station.location()).isEqualTo(location);
        assertThat(station.capacity()).isEqualTo(capacity);
    }

    @Test
    void shouldThrowExceptionWhenLocationIsNull() {
        // Given
        StationLocation location = null;
        String name = "Station Test";
        int capacity = 10;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            new Station(location, name, capacity);
        });
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // Given
        StationLocation location = new StationLocation("46.78,-71.29");
        String name = null;
        int capacity = 10;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            new Station(location, name, capacity);
        });
    }

    @Test
    void shouldThrowExceptionWhenCapacityIsZero() {
        // Given
        StationLocation location = new StationLocation("46.78,-71.29");
        String name = "Station Test";
        int capacity = 0;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Station(location, name, capacity);
        });
    }

    @Test
    void shouldThrowExceptionWhenCapacityIsNegative() {
        // Given
        StationLocation location = new StationLocation("46.78,-71.29");
        String name = "Station Test";
        int capacity = -5;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Station(location, name, capacity);
        });
    }

    @Test
    void shouldCreateStationSnapshot() {
        // Given
        StationLocation location = new StationLocation("46.78,-71.29");
        String name = "Station Test";
        int capacity = 10;
        Station station = new Station(location, name, capacity);

        // When
        StationSnapshot snapshot = station.snapshot();

        // Then
        assertThat(snapshot).isNotNull();
        assertThat(snapshot.location()).isEqualTo(location);
        assertThat(snapshot.name()).isEqualTo(name);
        assertThat(snapshot.capacity()).isEqualTo(capacity);
    }

    @Test
    void shouldHaveCorrectToString() {
        // Given
        StationLocation location = new StationLocation("46.78,-71.29");
        String name = "Station Test";
        int capacity = 10;
        Station station = new Station(location, name, capacity);

        // When
        String result = station.toString();

        // Then
        assertThat(result).contains("Station");
        assertThat(result).contains("location=");
        assertThat(result).contains("name=");
        assertThat(result).contains("capacity=");
    }
}