package com.vfi.android.emvkernel.corelogical.states.common;

import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;

import static com.vfi.android.emvkernel.corelogical.states.base.EmvStateType.*;
import static com.vfi.android.emvkernel.corelogical.states.base.EventType.*;

public class IdleState extends AbstractEmvState {
    public IdleState() {
        super(STATE_IDLE);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        if (getEventType().equals(EV_START_EMV_FLOW)) {
            jumpToState(STATE_SELECT_APP);
        }
    }
}
