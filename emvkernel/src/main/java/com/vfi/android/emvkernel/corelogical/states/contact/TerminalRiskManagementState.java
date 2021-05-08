package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;

public class TerminalRiskManagementState extends AbstractEmvState {
    public TerminalRiskManagementState() {
        super(EmvStateType.STATE_TERMINAL_RISK_MANAGEMENT);
    }
}
