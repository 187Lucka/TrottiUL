package ca.ulaval.trotti_ul.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import ca.ulaval.trotti_ul.api.station.StationResource;
import ca.ulaval.trotti_ul.api.station.dto.StationSummaryResponse;
import ca.ulaval.trotti_ul.application.station.GetStationDetailsUseCase;
import ca.ulaval.trotti_ul.application.station.GetStationsUseCase;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationSnapshot;
import ca.ulaval.trotti_ul.domain.scooter.ScooterSnapshot;
import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
class StationIntegrationTest {

    @Mock
    private GetStationsUseCase getStationsUseCase;

    @Mock
    private GetStationDetailsUseCase getStationDetailsUseCase;

    @InjectMocks
    private StationResource stationResource;

    @Test
    void shouldIntegrateStationWorkflow() {
        // Given - Setup mock data
        StationLocation location1 = new StationLocation("46.78,-71.29");
        StationLocation location2 = new StationLocation("46.78,-71.29");
        
        StationSnapshot snapshot1 = new StationSnapshot(location1, "Station 1", 10);
        StationSnapshot snapshot2 = new StationSnapshot(location2, "Station 2", 15);
        
        GetStationsUseCase.StationSnapshotWithCount dto1 = new GetStationsUseCase.StationSnapshotWithCount(snapshot1, 5);
        GetStationsUseCase.StationSnapshotWithCount dto2 = new GetStationsUseCase.StationSnapshotWithCount(snapshot2, 8);
        
        List<GetStationsUseCase.StationSnapshotWithCount> stations = List.of(dto1, dto2);
        
        when(getStationsUseCase.handle()).thenReturn(stations);

        // When - Get all stations
        Response stationsResponse = stationResource.getStations();

        // Then - Verify stations response
        assertThat(stationsResponse).isNotNull();
        assertThat(stationsResponse.getStatus()).isEqualTo(200);
        
        List<StationSummaryResponse> stationsResult = (List<StationSummaryResponse>) stationsResponse.getEntity();
        assertThat(stationsResult).hasSize(2);
        
        // Given - Setup scooter data for first station
        ScooterSnapshot slot1 = new ScooterSnapshot("scooter-1", location1, 1, 85, true);
        ScooterSnapshot slot2 = new ScooterSnapshot("scooter-1", location1, 2, 60, false);
        
        GetStationDetailsUseCase.StationDetails detailsDto = new GetStationDetailsUseCase.StationDetails(
            snapshot1,
            List.of(slot1, slot2)
        );
        
        when(getStationDetailsUseCase.handle("46.78,-71.29")).thenReturn(java.util.Optional.of(detailsDto));

        // When - Get scooters for first station
        Response scootersResponse = stationResource.getScooters("46.78,-71.29");

        // Then - Verify scooters response
        assertThat(scootersResponse).isNotNull();
        assertThat(scootersResponse.getStatus()).isEqualTo(200);
        
        List<?> scootersResult = (List<?>) scootersResponse.getEntity();
        assertThat(scootersResult).hasSize(2);
        
        // Verify all interactions
        verify(getStationsUseCase, times(1)).handle();
        verify(getStationDetailsUseCase, times(1)).handle("46.78,-71.29");
    }

    @Test
    void shouldHandleCompleteStationWorkflowWithErrorCases() {
        // Given - Empty stations
        when(getStationsUseCase.handle()).thenReturn(List.of());

        // When - Get all stations (empty)
        Response emptyStationsResponse = stationResource.getStations();

        // Then - Verify empty stations response
        assertThat(emptyStationsResponse).isNotNull();
        assertThat(emptyStationsResponse.getStatus()).isEqualTo(200);
        
        List<StationSummaryResponse> emptyResult = (List<StationSummaryResponse>) emptyStationsResponse.getEntity();
        assertThat(emptyResult).isEmpty();

        // Given - Unknown station
        when(getStationDetailsUseCase.handle("0,0")).thenReturn(java.util.Optional.empty());

        // When - Get scooters for unknown station
        Response notFoundResponse = stationResource.getScooters("0,0");

        // Then - Verify not found response
        assertThat(notFoundResponse).isNotNull();
        assertThat(notFoundResponse.getStatus()).isEqualTo(404);

        // Given - Invalid location
        Response badRequestResponse = stationResource.getScooters(null);

        // Then - Verify bad request response
        assertThat(badRequestResponse).isNotNull();
        assertThat(badRequestResponse.getStatus()).isEqualTo(400);
        
        String errorResult = (String) badRequestResponse.getEntity();
        assertThat(errorResult).contains("INVALID_LOCATION");
    }

    @Test
    void shouldHandleMultipleStationRequests() {
        // Given - Setup multiple stations
        StationLocation location1 = new StationLocation("46.78,-71.29");
        StationLocation location2 = new StationLocation("46.78,-71.29");
        StationLocation location3 = new StationLocation("46.78,-71.29");
        
        StationSnapshot snapshot1 = new StationSnapshot(location1, "Station 1", 10);
        StationSnapshot snapshot2 = new StationSnapshot(location2, "Station 2", 15);
        StationSnapshot snapshot3 = new StationSnapshot(location3, "Station 3", 20);
        
        GetStationsUseCase.StationSnapshotWithCount dto1 = new GetStationsUseCase.StationSnapshotWithCount(snapshot1, 5);
        GetStationsUseCase.StationSnapshotWithCount dto2 = new GetStationsUseCase.StationSnapshotWithCount(snapshot2, 8);
        GetStationsUseCase.StationSnapshotWithCount dto3 = new GetStationsUseCase.StationSnapshotWithCount(snapshot3, 12);
        
        List<GetStationsUseCase.StationSnapshotWithCount> stations = List.of(dto1, dto2, dto3);
        
        when(getStationsUseCase.handle()).thenReturn(stations);

        // When - Get all stations multiple times
        Response response1 = stationResource.getStations();
        Response response2 = stationResource.getStations();
        Response response3 = stationResource.getStations();

        // Then - Verify all responses are consistent
        assertThat(response1.getStatus()).isEqualTo(200);
        assertThat(response2.getStatus()).isEqualTo(200);
        assertThat(response3.getStatus()).isEqualTo(200);
        
        List<StationSummaryResponse> result1 = (List<StationSummaryResponse>) response1.getEntity();
        List<StationSummaryResponse> result2 = (List<StationSummaryResponse>) response2.getEntity();
        List<StationSummaryResponse> result3 = (List<StationSummaryResponse>) response3.getEntity();
        
        assertThat(result1).hasSize(3);
        assertThat(result2).hasSize(3);
        assertThat(result3).hasSize(3);
        
        // Verify that the use case was called exactly 3 times
        verify(getStationsUseCase, times(3)).handle();
    }
}