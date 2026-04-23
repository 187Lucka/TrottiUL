package ca.ulaval.trotti_ul.api.ride;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import ca.ulaval.trotti_ul.api.ride.dto.EndRideRequest;
import ca.ulaval.trotti_ul.api.ride.dto.GenerateCodeResponse;
import ca.ulaval.trotti_ul.api.ride.dto.RideResponse;
import ca.ulaval.trotti_ul.api.ride.dto.StartRideRequest;
import ca.ulaval.trotti_ul.application.ride.EndRideUseCase;
import ca.ulaval.trotti_ul.application.ride.GenerateCodeUseCase;
import ca.ulaval.trotti_ul.application.ride.GetRideHistoryUseCase;
import ca.ulaval.trotti_ul.application.ride.StartRideUseCase;
import ca.ulaval.trotti_ul.domain.ride.GenerateCode;
import ca.ulaval.trotti_ul.domain.ride.Ride;
import ca.ulaval.trotti_ul.domain.ride.RideId;
import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RideResourceTest {

    @Mock
    private GenerateCodeUseCase generateCodeUseCase;

    @Mock
    private StartRideUseCase startRideUseCase;

    @Mock
    private EndRideUseCase endRideUseCase;

    @Mock
    private GetRideHistoryUseCase getRideHistoryUseCase;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private RideResource rideResource;

    @Test
    void generateCode_shouldReturnUnlockCode_whenUserIsAuthenticated() {
        // Given: Un utilisateur authentifié souhaite générer un code de déverrouillage
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        GenerateCode code = new GenerateCode("12345", Instant.now().plusSeconds(300));

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(generateCodeUseCase.handle(accountId)).thenReturn(code);

        // When: L'utilisateur demande un code de déverrouillage
        Response response = rideResource.generateCode(securityContext);

        // Then: Le système retourne un code valide avec un statut 201 (Created)
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(201);

        GenerateCodeResponse result = (GenerateCodeResponse) response.getEntity();
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("12345");
        assertThat(result.expiresAt()).isNotNull();

        verify(generateCodeUseCase, times(1)).handle(accountId);
    }

    @Test
    void generateCode_shouldHandleNullUserGracefully_whenUserIsNotAuthenticated() {
        // Given: Un utilisateur non authentifié tente de générer un code
        when(securityContext.getUserPrincipal()).thenReturn(() -> null);
        when(generateCodeUseCase.handle(null)).thenReturn(new GenerateCode("0000", Instant.now().plusSeconds(300)));

        // When: Le système traite la requête
        Response response = rideResource.generateCode(securityContext);

        // Then: Le système retourne une réponse valide même pour un utilisateur non authentifié
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(201);

        GenerateCodeResponse result = (GenerateCodeResponse) response.getEntity();
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("0000");

        verify(generateCodeUseCase, times(1)).handle(null);
    }

    @Test
    void startRide_shouldSuccessfullyUnlockBike_whenCodeIsValid() {
        // Given: Un utilisateur authentifié avec un code valide souhaite démarrer un trajet
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        StartRideRequest request = new StartRideRequest("ABC123", "STATION_A", 5);

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        // Le use case ne retourne rien, donc pas de mock nécessaire pour le retour
        doNothing().when(startRideUseCase).handle(eq(accountId), eq("ABC123"), eq("STATION_A"), eq(5));

        // When: L'utilisateur démarre un trajet avec un code valide
        Response response = rideResource.startRide(securityContext, request);

        // Then: Le système confirme le déverrouillage avec un statut 204 (No Content)
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(204);

        verify(startRideUseCase, times(1)).handle(eq(accountId), eq("ABC123"), eq("STATION_A"), eq(5));
    }

    @Test
    void startRide_shouldThrowException_whenCodeIsInvalid() {
        // Given: Un utilisateur tente de démarrer un trajet avec un code invalide
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        StartRideRequest request = new StartRideRequest("INVALID", "STATION_A", 5);

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        doThrow(new IllegalArgumentException("Invalid unlock code"))
            .when(startRideUseCase).handle(eq(accountId), eq("INVALID"), eq("STATION_A"), eq(5));

        // When/Then: Le système rejette la tentative avec une exception
        assertThatThrownBy(() -> rideResource.startRide(securityContext, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid unlock code");
    }

    @Test
    void startRide_shouldThrowException_whenBikeIsAlreadyUnlocked() {
        // Given: Un utilisateur tente de démarrer un trajet alors qu'un vélo est déjà déverrouillé
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        StartRideRequest request = new StartRideRequest("ABC123", "STATION_A", 5);

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        doThrow(new IllegalStateException("Bike already unlocked"))
            .when(startRideUseCase).handle(eq(accountId), eq("ABC123"), eq("STATION_A"), eq(5));

        // When/Then: Le système rejette la tentative car un vélo est déjà en cours d'utilisation
        assertThatThrownBy(() -> rideResource.startRide(securityContext, request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Bike already unlocked");
    }

    @Test
    void endRide_shouldSuccessfullyCompleteTrip_whenBikeIsReturned() {
        // Given: Un utilisateur authentifié termine un trajet en retournant un vélo
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        EndRideRequest request = new EndRideRequest("STATION_B", 3);
        
        // Créer un trajet commencé puis terminé
        Ride startedRide = Ride.start(
            AccountId.fromString(accountId),
            StationLocation.fromString("STATION_A"),
            Instant.now().minus(java.time.Duration.ofMinutes(30)),
            95 // Niveau d'énergie de départ
        );
        
        Ride completedRide = startedRide.finish(
            StationLocation.fromString("STATION_B"),
            Instant.now(),
            0, // Pas de frais supplémentaires
            80  // Niveau d'énergie à la fin
        );

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(endRideUseCase.handle(eq(accountId), eq("STATION_B"), eq(3))).thenReturn(completedRide);

        // When: L'utilisateur termine son trajet
        Response response = rideResource.endRide(securityContext, request);

        // Then: Le système confirme la fin du trajet et retourne les détails
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);

        RideResponse result = (RideResponse) response.getEntity();
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(completedRide.id().value().toString());
        assertThat(result.startStation()).isEqualTo("STATION_A");
        assertThat(result.endStation()).isEqualTo("STATION_B");
        assertThat(result.startTime()).isNotNull();
        assertThat(result.endTime()).isNotNull();

        verify(endRideUseCase, times(1)).handle(eq(accountId), eq("STATION_B"), eq(3));
    }

    @Test
    void endRide_shouldThrowException_whenNoActiveRideExists() {
        // Given: Un utilisateur tente de terminer un trajet alors qu'aucun trajet actif n'existe
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        EndRideRequest request = new EndRideRequest("STATION_B", 3);

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        doThrow(new IllegalStateException("No active ride to end"))
            .when(endRideUseCase).handle(eq(accountId), eq("STATION_B"), eq(3));

        // When/Then: Le système rejette la tentative car aucun trajet n'est en cours
        assertThatThrownBy(() -> rideResource.endRide(securityContext, request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("No active ride to end");
    }

    @Test
    void endRide_shouldThrowException_whenBikeIsNotReturnedToValidStation() {
        // Given: Un utilisateur tente de terminer un trajet à une station invalide
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        EndRideRequest request = new EndRideRequest("INVALID_STATION", 99);

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        doThrow(new IllegalArgumentException("Invalid return location"))
            .when(endRideUseCase).handle(eq(accountId), eq("INVALID_STATION"), eq(99));

        // When/Then: Le système rejette la tentative car la station de retour est invalide
        assertThatThrownBy(() -> rideResource.endRide(securityContext, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid return location");
    }

    @Test
    void history_shouldReturnCompleteRideHistory_whenUserHasPreviousRides() {
        // Given: Un utilisateur authentifié avec un historique de trajets
        String accountId = "123e4567-e89b-12d3-a456-426614174000";
        
        // Créer des trajets pour l'historique
        Ride ride1 = Ride.start(
            AccountId.fromString(accountId),
            StationLocation.fromString("STATION_A"),
            Instant.now().minus(java.time.Duration.ofDays(2)),
            90
        ).finish(
            StationLocation.fromString("STATION_B"),
            Instant.now().minus(java.time.Duration.ofDays(2)).plus(java.time.Duration.ofMinutes(45)),
            0,
            75
        );

        Ride ride2 = Ride.start(
            AccountId.fromString(accountId),
            StationLocation.fromString("STATION_C"),
            Instant.now().minus(java.time.Duration.ofDays(1)),
            85
        ).finish(
            StationLocation.fromString("STATION_D"),
            Instant.now().minus(java.time.Duration.ofDays(1)).plus(java.time.Duration.ofMinutes(30)),
            50, // Petit supplément pour dépassement
            70
        );

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(getRideHistoryUseCase.handle(accountId)).thenReturn(List.of(ride1, ride2));

        // When: L'utilisateur demande son historique de trajets
        Response response = rideResource.history(securityContext);

        // Then: Le système retourne l'historique complet des trajets
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);

        List<RideResponse> result = (List<RideResponse>) response.getEntity();
        assertThat(result).hasSize(2);
        
        // Vérification du premier trajet
        RideResponse firstRide = result.get(0);
        assertThat(firstRide.id()).isEqualTo(ride1.id().value().toString());
        assertThat(firstRide.startStation()).isEqualTo("STATION_A");
        assertThat(firstRide.endStation()).isEqualTo("STATION_B");
        
        // Vérification du deuxième trajet
        RideResponse secondRide = result.get(1);
        assertThat(secondRide.id()).isEqualTo(ride2.id().value().toString());
        assertThat(secondRide.startStation()).isEqualTo("STATION_C");
        assertThat(secondRide.endStation()).isEqualTo("STATION_D");

        verify(getRideHistoryUseCase, times(1)).handle(accountId);
    }

    @Test
    void history_shouldReturnEmptyList_whenUserHasNoPreviousRides() {
        // Given: Un nouvel utilisateur sans historique de trajets
        String accountId = "123e4567-e89b-12d3-a456-426614174000";

        when(securityContext.getUserPrincipal()).thenReturn(() -> accountId);
        when(getRideHistoryUseCase.handle(accountId)).thenReturn(List.of());

        // When: L'utilisateur demande son historique de trajets
        Response response = rideResource.history(securityContext);

        // Then: Le système retourne une liste vide
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);

        List<RideResponse> result = (List<RideResponse>) response.getEntity();
        assertThat(result).isEmpty();

        verify(getRideHistoryUseCase, times(1)).handle(accountId);
    }

    @Test
    void history_shouldHandleNullUserGracefully_whenUserIsNotAuthenticated() {
        // Given: Un utilisateur non authentifié tente d'accéder à l'historique
        when(securityContext.getUserPrincipal()).thenReturn(() -> null);
        when(getRideHistoryUseCase.handle(null)).thenReturn(List.of());

        // When: Le système traite la requête
        Response response = rideResource.history(securityContext);

        // Then: Le système retourne une réponse valide (liste vide) même pour un utilisateur non authentifié
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat((List<?>) response.getEntity()).isEmpty();

        verify(getRideHistoryUseCase, times(1)).handle(null);
    }
}