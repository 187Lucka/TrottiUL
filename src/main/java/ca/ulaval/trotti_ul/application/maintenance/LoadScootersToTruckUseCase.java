package ca.ulaval.trotti_ul.application.maintenance;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.maintenance.NotATechnicianException;
import ca.ulaval.trotti_ul.domain.maintenance.TechnicianTruck;
import ca.ulaval.trotti_ul.domain.maintenance.TechnicianTruckRepository;
import ca.ulaval.trotti_ul.domain.maintenance.TransferredScooter;
import ca.ulaval.trotti_ul.domain.scooter.ScooterEnergyService;
import ca.ulaval.trotti_ul.domain.scooter.ScooterReservation;
import ca.ulaval.trotti_ul.domain.station.Station;
import ca.ulaval.trotti_ul.domain.station.StationLocation;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.station.UnknownStationException;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;

public class LoadScootersToTruckUseCase {

    private final TechnicianRepository technicianRepository;
    private final TechnicianTruckRepository technicianTruckRepository;
    private final StationRepository stationRepository;
    private final ScooterReservation scooterReservation;
    private final ScooterEnergyService scooterEnergyService;

    public LoadScootersToTruckUseCase(TechnicianRepository technicianRepository,
                                      TechnicianTruckRepository technicianTruckRepository,
                                      StationRepository stationRepository,
                                      ScooterReservation scooterReservation,
                                      ScooterEnergyService scooterEnergyService) {
        this.technicianRepository = technicianRepository;
        this.technicianTruckRepository = technicianTruckRepository;
        this.stationRepository = stationRepository;
        this.scooterReservation = scooterReservation;
        this.scooterEnergyService = scooterEnergyService;
    }

    public List<TransferredScooter> handle(String accountIdString, String stationLocationName, List<Integer> slotNumbers) {
        AccountId accountId = AccountId.fromString(accountIdString);
        Technician technician = requireTechnician(accountId);
        StationLocation stationLocation = resolveStation(stationLocationName);

        TechnicianTruck truck = technicianTruckRepository.getOrCreate(technician.id());

        List<TransferredScooter> loadedScooters = new ArrayList<>();

        for (int slotNumber : slotNumbers) {
            String scooterId = stationLocation.value() + "-S" + slotNumber;
            scooterEnergyService.applyRechargeIfAllowed(stationLocation);
            int energy = scooterReservation.removeForTransfer(stationLocation, slotNumber);

            TransferredScooter scooter = new TransferredScooter(
                    scooterId,
                    stationLocation,
                    slotNumber,
                    energy
            );
            truck.loadScooter(scooter);
            loadedScooters.add(scooter);
        }

        technicianTruckRepository.save(truck);

        return loadedScooters;
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
