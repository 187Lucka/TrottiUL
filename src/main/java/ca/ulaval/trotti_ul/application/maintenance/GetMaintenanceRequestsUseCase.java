package ca.ulaval.trotti_ul.application.maintenance;

import java.util.List;

import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestNotFoundException;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequest;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestId;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestRepository;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestStatus;

public class GetMaintenanceRequestsUseCase {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public GetMaintenanceRequestsUseCase(MaintenanceRequestRepository maintenanceRequestRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    public List<MaintenanceRequest> handleGetAll() {
        return maintenanceRequestRepository.findAll();
    }

    public List<MaintenanceRequest> handleGetPending() {
        return maintenanceRequestRepository.findAll().stream()
                .filter(request -> request.status() == MaintenanceRequestStatus.PENDING)
                .toList();
    }

    public MaintenanceRequest handleGetById(String requestIdString) {
        MaintenanceRequestId requestId = MaintenanceRequestId.fromString(requestIdString);
        return maintenanceRequestRepository.findById(requestId)
                .orElseThrow(MaintenanceRequestNotFoundException::new);
    }
}
