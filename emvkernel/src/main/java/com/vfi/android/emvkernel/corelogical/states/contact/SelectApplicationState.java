package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;

import static com.vfi.android.emvkernel.corelogical.states.base.EmvStateType.*;

public class SelectApplicationState extends AbstractEmvState {
    public SelectApplicationState() {
        super(STATE_SELECT_APP);
    }

    @Override
    public void run(EmvContext context) {

    }
}
