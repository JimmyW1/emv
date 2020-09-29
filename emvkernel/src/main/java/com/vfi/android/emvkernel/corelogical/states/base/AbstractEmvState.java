package com.vfi.android.emvkernel.corelogical.states.base;

import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;

public abstract class AbstractEmvState implements IEmvState {
    protected final String TAG = TAGS.EMV_STATE;
    private EmvContext emvContext;
    private String stateType;

    public AbstractEmvState(String stateType) {
        this.stateType = stateType;
    }

    protected void jumpToState(String stateType) {
        if (emvContext != null && emvContext.getBaseEmvFlow() != null) {
            emvContext.getBaseEmvFlow().jumpToState(stateType);
        }
    }

    public String getStateType() {
        return stateType;
    }

    protected String getEventType() {
        return emvContext.getEventType();
    }

    @Override
    public void run(EmvContext context) {
        this.emvContext = context;
        LogUtil.d(TAG, "State[" + stateType + "] EventType=[" + context.getEventType() + "]");

    }
}
