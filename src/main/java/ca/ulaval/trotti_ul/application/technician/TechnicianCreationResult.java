package ca.ulaval.trotti_ul.application.technician;

import ca.ulaval.trotti_ul.domain.account.Account;
import ca.ulaval.trotti_ul.domain.technician.TechnicianId;

public record TechnicianCreationResult(TechnicianId technicianId, Account account) { }
