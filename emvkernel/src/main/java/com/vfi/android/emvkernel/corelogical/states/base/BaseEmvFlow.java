package com.vfi.android.emvkernel.corelogical.states.base;

import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.base.ToAppMessage;
import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;
import com.vfi.android.emvkernel.corelogical.states.common.IdleState;
import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;

import java.util.List;

public abstract class BaseEmvFlow {
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

    public abstract void jumpToState(String stateType);

    public void runCurrentState(Message message) {
        if (message == null) {
            return;
        }

        LogUtil.d(TAG, "runCurrentState messageType=" + message.getMessageType());

        if (message instanceof ToEmvMessage) {
            if (currentEmvState != null) {
                emvContext.setMessage(message);
                currentEmvState.run(emvContext);
            }
        } else if (message instanceof ToAppMessage) {

        } else {

        }
    }

    public void sendMessage(Message message) {
        runCurrentState(message);
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

    public void setEmvTag(String tag, String value) {

    }

    public String getEmvTag(String tag) {
        return null;
    }

    public String getEmvTags(List<String> tagList) {
        return null;
    }

    public void saveEmvAppParamList(int groupId, List<String> emvAppList) {

    }

    public List<String> getEmvAppParamList(int groupId) {
        return null;
    }

    public void clearAllEmvAppParams() {

    }

    public void saveEmvKeyParamList(int groupId, List<String> emvKeyList) {

    }

    public List<String> getEmvKeyParamList(int groupId) {
        return null;
    }

    public void clearAllEmvKeyParams() {

    }
}
