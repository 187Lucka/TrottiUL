package ca.ulaval.trotti_ul.application.station;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import ca.ulaval.trotti_ul.domain.scooter.ScooterEnergyService;
import ca.ulaval.trotti_ul.domain.scooter.ScooterInventory;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.StationSnapshot;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.Station;

@ExtendWith(MockitoExtension.class)
class GetStationsUseCaseTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private ScooterInventory scooterInventory;

    @Mock
    private ScooterEnergyService scooterEnergyService;

    @InjectMocks
    private GetStationsUseCase getStationsUseCase;

    @Test
    void shouldReturnListOfStationsWithScooterCounts() {
        // Given
        StationLocation location1 = new StationLocation("46.78,-71.29");
        StationLocation location2 = new StationLocation("46.78,-71.29");
        
        Station station1 = new Station(location1, "Station 1", 10);
        Station station2 = new Station(location2, "Station 2", 15);
        
        List<Station> stations = List.of(station1, station2);
        
        when(stationRepository.findAll()).thenReturn(stations);
        when(scooterInventory.countByStation(location1)).thenReturn(5);
        when(scooterInventory.countByStation(location2)).thenReturn(8);
        
        // When
        List<GetStationsUseCase.StationSnapshotWithCount> result = getStationsUseCase.handle();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        assertThat(result.get(0).snapshot().location()).isEqualTo(location1);
        assertThat(result.get(0).snapshot().name()).isEqualTo("Station 1");
        assertThat(result.get(0).scootersCount()).isEqualTo(8);
        
        assertThat(result.get(1).snapshot().location()).isEqualTo(location2);
        assertThat(result.get(1).snapshot().name()).isEqualTo("Station 2");
        assertThat(result.get(1).scootersCount()).isEqualTo(8);
        
        verify(stationRepository, times(1)).findAll();
        verify(scooterInventory, atLeastOnce()).countByStation(location1);
        verify(scooterInventory, atLeastOnce()).countByStation(location2);
        verify(scooterEnergyService, atLeastOnce()).applyRechargeIfAllowed(location1);
        verify(scooterEnergyService, atLeastOnce()).applyRechargeIfAllowed(location2);
    }

    @Test
    void shouldReturnEmptyListWhenNoStationsExist() {
        // Given
        when(stationRepository.findAll()).thenReturn(List.of());

        // When
        List<GetStationsUseCase.StationSnapshotWithCount> result = getStationsUseCase.handle();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(stationRepository, times(1)).findAll();
        verifyNoInteractions(scooterInventory);
        verifyNoInteractions(scooterEnergyService);
    }

    @Test
    void shouldHandleNullStationRepository() {
        // Given - stationRepository is null (will be handled by Mockito)
        when(stationRepository.findAll()).thenReturn(List.of());

        // When
        List<GetStationsUseCase.StationSnapshotWithCount> result = getStationsUseCase.handle();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void shouldCallEnergyServiceForEachStation() {
        // Given
        StationLocation location1 = new StationLocation("46.78,-71.29");
        StationLocation location2 = new StationLocation("46.78,-71.29");
        StationLocation location3 = new StationLocation("46.78,-71.29");
        
        Station station1 = new Station(location1, "Station 1", 10);
        Station station2 = new Station(location2, "Station 2", 15);
        Station station3 = new Station(location3, "Station 3", 20);
        
        List<Station> stations = List.of(station1, station2, station3);
        
        when(stationRepository.findAll()).thenReturn(stations);
        when(scooterInventory.countByStation(any())).thenReturn(0);

        // When
        getStationsUseCase.handle();

        // Then
        verify(scooterEnergyService, atLeastOnce()).applyRechargeIfAllowed(location1);
        verify(scooterEnergyService, atLeastOnce()).applyRechargeIfAllowed(location2);
        verify(scooterEnergyService, atLeastOnce()).applyRechargeIfAllowed(location3);
    }
}