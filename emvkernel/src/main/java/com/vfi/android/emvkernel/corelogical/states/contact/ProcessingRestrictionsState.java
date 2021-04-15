package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;

public class ProcessingRestrictionsState extends AbstractEmvState {
    public ProcessingRestrictionsState() {
        super(EmvStateType.STATE_PROCESSING_RESTRICTIONS);
    }
}
