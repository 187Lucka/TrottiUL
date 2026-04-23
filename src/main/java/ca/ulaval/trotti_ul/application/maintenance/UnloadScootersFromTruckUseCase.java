package ca.ulaval.trotti_ul.application.maintenance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.maintenance.NotATechnicianException;
import ca.ulaval.trotti_ul.domain.maintenance.ScooterNotInTruckException;
import ca.ulaval.trotti_ul.domain.maintenance.SlotNotEmptyException;
import ca.ulaval.trotti_ul.domain.maintenance.TechnicianTruck;
import ca.ulaval.trotti_ul.domain.maintenance.TechnicianTruckRepository;
import ca.ulaval.trotti_ul.domain.maintenance.TransferredScooter;
import ca.ulaval.trotti_ul.domain.scooter.ScooterEnergyService;
import ca.ulaval.trotti_ul.domain.scooter.ScooterInventory;
import ca.ulaval.trotti_ul.domain.scooter.ScooterReservation;
import ca.ulaval.trotti_ul.domain.station.Station;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.StationUnderMaintenanceException;
import ca.ulaval.trotti_ul.domain.station.UnknownStationException;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;

public class UnloadScootersFromTruckUseCase {

    private final TechnicianRepository technicianRepository;
    private final TechnicianTruckRepository technicianTruckRepository;
    private final StationRepository stationRepository;
    private final ScooterInventory scooterInventory;
    private final ScooterReservation scooterReservation;
    private final ScooterEnergyService scooterEnergyService;

    public UnloadScootersFromTruckUseCase(TechnicianRepository technicianRepository,
                                          TechnicianTruckRepository technicianTruckRepository,
                                          StationRepository stationRepository,
                                          ScooterInventory scooterInventory,
                                          ScooterReservation scooterReservation,
                                          ScooterEnergyService scooterEnergyService) {
        this.technicianRepository = technicianRepository;
        this.technicianTruckRepository = technicianTruckRepository;
        this.stationRepository = stationRepository;
        this.scooterInventory = scooterInventory;
        this.scooterReservation = scooterReservation;
        this.scooterEnergyService = scooterEnergyService;
    }

    public List<TransferredScooter> handle(String accountIdString,
                                           String destinationStationName,
                                           Map<String, Integer> placements) {
        AccountId accountId = AccountId.fromString(accountIdString);
        Technician technician = requireTechnician(accountId);
        StationLocation destinationStation = resolveStation(destinationStationName);

        if (stationRepository.isUnderMaintenance(destinationStation)) {
            throw new StationUnderMaintenanceException(destinationStation.value());
        }

        TechnicianTruck truck = technicianTruckRepository.getOrCreate(technician.id());
        scooterEnergyService.applyRechargeIfAllowed(destinationStation);

        List<TransferredScooter> unloadedScooters = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : placements.entrySet()) {
            String scooterId = entry.getKey();
            int targetSlot = entry.getValue();

            if (!scooterInventory.isSlotEmpty(destinationStation, targetSlot)) {
                throw new SlotNotEmptyException(destinationStation.value(), targetSlot);
            }

            TransferredScooter scooter = truck.unloadScooter(scooterId)
                    .orElseThrow(() -> new ScooterNotInTruckException(scooterId));

            scooterReservation.returnScooter(destinationStation, targetSlot, scooter.energyPercent());

            unloadedScooters.add(scooter);
        }

        technicianTruckRepository.save(truck);

        return unloadedScooters;
    }

    private Technician requireTechnician(AccountId accountId) {
        return technicianRepository.findByAccountId(accountId)
                .filter(Technician::isActive)
                .orElseThrow(NotATechnicianException::new);
    }

    private StationLocation resolveStation(String stationLocationName) {
        return stationRepository.findByLocationName(stationLocationName)
                .map(Station::location)
                .orElseThrow(() -> new UnknownStationException(stationLocationName));
    }
}
