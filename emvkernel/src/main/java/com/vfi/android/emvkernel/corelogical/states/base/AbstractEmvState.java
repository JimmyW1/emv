package com.vfi.android.emvkernel.corelogical.states.base;

import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.emvkernel.data.beans.EmvApplication;
import com.vfi.android.emvkernel.data.beans.EmvTransData;
import com.vfi.android.libtools.consts.TAGS;

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

    protected byte[] executeApduCmd(ApduCmd apduCmd) {
        if (emvContext != null && emvContext.getEmvComm() != null) {
            return emvContext.getEmvComm().executeApduCmd(apduCmd);
        }

        return null;
    }

    protected void sendMessage(Message message) {
        if (emvContext != null && emvContext.getBaseEmvFlow() != null) {
            emvContext.getBaseEmvFlow().sendMessage(message);
        }
    }

    @Override
    public void run(EmvContext context) {
        this.emvContext = context;
    }

    public EmvContext getEmvContext() {
        return emvContext;
    }

    public EmvTransData getEmvTransData() {
        return emvContext.getCurrentTransData();
    }

    public void addCandidateApplication(EmvApplication emvApplication) {
        getEmvTransData().getCandidateList().add(emvApplication);
    }

    public void clearCandidateList() {
        getEmvTransData().getCandidateList().clear();
    }
}
