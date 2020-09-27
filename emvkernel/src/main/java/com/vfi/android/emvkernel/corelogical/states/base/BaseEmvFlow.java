package com.vfi.android.emvkernel.corelogical.states.base;

import com.vfi.android.emvkernel.corelogical.states.common.IdleState;
import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;

public class BaseEmvFlow {
    protected final String TAG = TAGS.EMV_COMM;
    private EmvContext emvContext;
    private IEmvState currentEmvState;

    public BaseEmvFlow(EmvContext emvContext) {
        this.emvContext = emvContext;
        initStateMachine();
    }

    private void initStateMachine() {
        LogUtil.d(TAG, "initStateMachine");
        emvContext.setBaseEmvFlow(this);
        currentEmvState = new IdleState();
    }

    public EmvContext getEmvContext() {
        return emvContext;
    }

    public IEmvState getCurrentEmvState() {
        return currentEmvState;
    }

    public void setCurrentEmvState(IEmvState currentEmvState) {
        this.currentEmvState = currentEmvState;
    }

    public void setEmvHandler(IEmvHandler iemvHandler) {
        if (emvContext != null) {
            emvContext.setEmvHandler(iemvHandler);
        }
    }

    public EmvParams getEmvParams() {
        return emvContext.getEmvParams();
    }

    public void setEmvParams(EmvParams emvParams) {
        emvContext.setEmvParams(emvParams);
    }
}
