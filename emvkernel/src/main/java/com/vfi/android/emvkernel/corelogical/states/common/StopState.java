package com.vfi.android.emvkernel.corelogical.states.common;

import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;

import static com.vfi.android.emvkernel.corelogical.states.base.EmvStateType.STATE_STOP;

public class StopState extends AbstractEmvState {
    public StopState() {
        super(STATE_STOP);
    }
}
