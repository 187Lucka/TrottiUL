package ca.ulaval.trotti_ul.domain.maintenance;

import ca.ulaval.trotti_ul.domain.common.BusinessException;

public class SlotNotEmptyException extends BusinessException {
    public SlotNotEmptyException(String station, int slot) {
        super("SLOT_NOT_EMPTY", "Slot " + slot + " is not empty at station " + station);
    }
}
