package ca.ulaval.trotti_ul.api.station;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import ca.ulaval.trotti_ul.api.station.dto.StationResponse;
import ca.ulaval.trotti_ul.api.station.dto.StationSummaryResponse;
import ca.ulaval.trotti_ul.application.station.GetStationDetailsUseCase;
import ca.ulaval.trotti_ul.application.station.GetStationsUseCase;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationSnapshot;
import ca.ulaval.trotti_ul.domain.station.Station;
import ca.ulaval.trotti_ul.domain.scooter.ScooterSnapshot;
import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
class StationResourceTest {

    @Mock
    private GetStationsUseCase getStationsUseCase;

    @Mock
    private GetStationDetailsUseCase getStationDetailsUseCase;

    @InjectMocks
    private StationResource stationResource;

    @Test
    void shouldReturnListOfStations() {
        // Given
        StationLocation location1 = new StationLocation("46.78,-71.29");
        StationLocation location2 = new StationLocation("46.78,-71.29");
        
        StationSnapshot snapshot1 = new StationSnapshot(location1, "Station 1", 10);
        StationSnapshot snapshot2 = new StationSnapshot(location2, "Station 2", 15);
        
        GetStationsUseCase.StationSnapshotWithCount dto1 = new GetStationsUseCase.StationSnapshotWithCount(snapshot1, 5);
        GetStationsUseCase.StationSnapshotWithCount dto2 = new GetStationsUseCase.StationSnapshotWithCount(snapshot2, 8);
        
        List<GetStationsUseCase.StationSnapshotWithCount> stations = List.of(dto1, dto2);
        
        when(getStationsUseCase.handle()).thenReturn(stations);

        // When
        Response response = stationResource.getStations();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);
        
        List<StationSummaryResponse> result = (List<StationSummaryResponse>) response.getEntity();
        assertThat(result).hasSize(2);
        
        assertThat(result.get(0).name()).isEqualTo("Station 1");
        assertThat(result.get(0).capacity()).isEqualTo(10);
        assertThat(result.get(0).scooters()).isEqualTo(5);
        
        assertThat(result.get(1).name()).isEqualTo("Station 2");
        assertThat(result.get(1).capacity()).isEqualTo(15);
        assertThat(result.get(1).scooters()).isEqualTo(8);
        
        verify(getStationsUseCase, times(1)).handle();
    }

    @Test
    void shouldReturnEmptyListWhenNoStations() {
        // Given
        when(getStationsUseCase.handle()).thenReturn(List.of());

        // When
        Response response = stationResource.getStations();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);
        
        List<StationSummaryResponse> result = (List<StationSummaryResponse>) response.getEntity();
        assertThat(result).isEmpty();
        
        verify(getStationsUseCase, times(1)).handle();
    }

    @Test
    void shouldReturnScootersForValidLocation() {
        // Given
        String location = "46.78,-71.29";
        StationLocation stationLocation = new StationLocation("46.78,-71.29");
        
        ScooterSnapshot slot1 = new ScooterSnapshot("scooter-1", stationLocation, 1, 85, true);
        ScooterSnapshot slot2 = new ScooterSnapshot("scooter-2", stationLocation, 2, 60, false);
        
        GetStationDetailsUseCase.StationDetails detailsDto = new GetStationDetailsUseCase.StationDetails(
            new StationSnapshot(stationLocation, "Station 1", 10),
            List.of(slot1, slot2)
        );
        
        when(getStationDetailsUseCase.handle(location)).thenReturn(Optional.of(detailsDto));

        // When
        Response response = stationResource.getScooters(location);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);
        
        List<StationResponse.ScooterSlotResponse> result = (List<StationResponse.ScooterSlotResponse>) response.getEntity();
        assertThat(result).hasSize(2);
        
        assertThat(result.get(0).slotNumber()).isEqualTo(1);
        assertThat(result.get(0).energyPercent()).isEqualTo(85);
        assertThat(result.get(0).occupied()).isTrue();
        
        assertThat(result.get(1).slotNumber()).isEqualTo(2);
        assertThat(result.get(1).energyPercent()).isEqualTo(60);
        assertThat(result.get(1).occupied()).isFalse();
        
        verify(getStationDetailsUseCase, times(1)).handle(location);
    }

    @Test
    void shouldReturnNotFoundForUnknownStation() {
        // Given
        String location = "0,0";
        
        when(getStationDetailsUseCase.handle(location)).thenReturn(Optional.empty());

        // When
        Response response = stationResource.getScooters(location);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(404);
        
        String result = (String) response.getEntity();
        assertThat(result).contains("UNKNOWN_STATION");
        assertThat(result).contains("Station not found");
        
        verify(getStationDetailsUseCase, times(1)).handle(location);
    }

    @Test
    void shouldReturnBadRequestForNullLocation() {
        // Given
        String location = null;

        // When
        Response response = stationResource.getScooters(location);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        
        String result = (String) response.getEntity();
        assertThat(result).contains("INVALID_LOCATION");
        assertThat(result).contains("location path param is required");
        
        verifyNoInteractions(getStationDetailsUseCase);
    }

    @Test
    void shouldReturnBadRequestForBlankLocation() {
        // Given
        String location = "   ";

        // When
        Response response = stationResource.getScooters(location);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        
        String result = (String) response.getEntity();
        assertThat(result).contains("INVALID_LOCATION");
        assertThat(result).contains("location path param is required");
        
        verifyNoInteractions(getStationDetailsUseCase);
    }

    @Test
    void shouldReturnBadRequestForEmptyLocation() {
        // Given
        String location = "";

        // When
        Response response = stationResource.getScooters(location);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);
        
        String result = (String) response.getEntity();
        assertThat(result).contains("INVALID_LOCATION");
        assertThat(result).contains("location path param is required");
        
        verifyNoInteractions(getStationDetailsUseCase);
    }
}