package ca.ulaval.trotti_ul.application.maintenance;

import java.util.List;

import ca.ulaval.trotti_ul.domain.account.AccountId;
import ca.ulaval.trotti_ul.domain.maintenance.NotATechnicianException;
import ca.ulaval.trotti_ul.domain.maintenance.TechnicianTruck;
import ca.ulaval.trotti_ul.domain.maintenance.TechnicianTruckRepository;
import ca.ulaval.trotti_ul.domain.maintenance.TransferredScooter;
import ca.ulaval.trotti_ul.domain.technician.Technician;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;

public class GetTruckContentsUseCase {

    private final TechnicianRepository technicianRepository;
    private final TechnicianTruckRepository technicianTruckRepository;

    public GetTruckContentsUseCase(TechnicianRepository technicianRepository,
                                   TechnicianTruckRepository technicianTruckRepository) {
        this.technicianRepository = technicianRepository;
        this.technicianTruckRepository = technicianTruckRepository;
    }

    public List<TransferredScooter> handle(String accountIdString) {
        AccountId accountId = AccountId.fromString(accountIdString);
        Technician technician = requireTechnician(accountId);

        TechnicianTruck truck = technicianTruckRepository.getOrCreate(technician.id());
        return truck.scooters();
    }

    private Technician requireTechnician(AccountId accountId) {
        return technicianRepository.findByAccountId(accountId)
                .filter(Technician::isActive)
                .orElseThrow(NotATechnicianException::new);
    }
}
