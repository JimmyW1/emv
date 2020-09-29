package com.vfi.android.emvkernel.corelogical;

import com.vfi.android.emvkernel.corelogical.states.base.BaseEmvFlow;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EventType;
import com.vfi.android.emvkernel.corelogical.states.base.IEmvState;
import com.vfi.android.emvkernel.corelogical.states.common.IdleState;
import com.vfi.android.emvkernel.corelogical.states.contact.SelectApplicationState;
import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.emvkernel.interfaces.IEmvOperation;
import com.vfi.android.libtools.utils.LogUtil;

import java.util.List;

import static com.vfi.android.emvkernel.corelogical.states.base.EmvStateType.*;

public class ContactEmvFlow extends BaseEmvFlow implements IEmvOperation {
    private EmvParams emvParams;

    public ContactEmvFlow(EmvContext emvContext) {
        super(emvContext);
    }

    @Override
    public void jumpToState(String stateType) {
        IEmvState emvState = null;

        LogUtil.d(TAG, "Current state=[" + getCurrentEmvState() + "] ---> State=[" + stateType + "]");

        switch (stateType) {
            case STATE_IDLE:
                emvState = new IdleState();
                break;
            case STATE_SELECT_APP:
                emvState = new SelectApplicationState();
                break;
        }

        if (emvState != null) {
            setCurrentEmvState(emvState);
        } else {
            LogUtil.d(TAG, "State[" + stateType + "] not found.");
        }
    }

    @Override
    public int initEmvFlow(EmvParams emvParams) {
        this.emvParams = emvParams;
        jumpToState(STATE_IDLE);
        return 0;
    }

    @Override
    public void startEMVFlow(IEmvHandler emvHandler) {
        setEmvHandler(emvHandler);
        sendEvent(EventType.EV_START_EMV_FLOW);
    }

    @Override
    public void stopEmvFlow() {
        jumpToState(STATE_IDLE);
    }

    @Override
    public void setEmvTag(String tag, String value) {

    }

    @Override
    public String getEmvTag(String tag) {
        return null;
    }

    @Override
    public String getEmvTags(List<String> tagList) {
        return null;
    }
}
