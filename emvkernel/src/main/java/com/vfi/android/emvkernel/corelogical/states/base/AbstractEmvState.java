package com.vfi.android.emvkernel.corelogical.states.base;

import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.emvkernel.data.beans.EmvApplication;
import com.vfi.android.emvkernel.data.beans.EmvTransData;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;

import java.util.Iterator;

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

    public IEmvHandler getEmvHandler() {
        return emvContext.getEmvHandler();
    }

    public void addCandidateApplication(EmvApplication emvApplication) {
        if (isDFNameExist(emvApplication.getDfName())) {
            LogUtil.d(TAG, "Skip tag4F[" + emvApplication.getDfName() + "] add to candidate list, exist in candidate now");
        } else {
            LogUtil.d(TAG, "tag4F[" + emvApplication.getDfName() + "] add to candidate list.");
            getEmvTransData().getCandidateList().add(emvApplication);
        }
    }

    public void removeCandidateApplication(String dfName) {
        Iterator<EmvApplication> iterator = getEmvTransData().getCandidateList().iterator();
        while (iterator.hasNext()) {
            EmvApplication emvApplication = iterator.next();
            if (emvApplication.getDfName().equals(dfName)) {
                iterator.remove();
                break;
            }
        }
    }

    public void clearCandidateList() {
        LogUtil.d(TAG, "clearCandidateList");
        getEmvTransData().getCandidateList().clear();
    }

    private boolean isDFNameExist(String dfName) {
        for (EmvApplication emvApplication : getEmvTransData().getCandidateList()) {
            if (dfName.equals(emvApplication.getDfName())) {
                return true;
            }
        }

        return false;
    }

    protected void setErrorCode(int errorCode) {
        getEmvTransData().setErrorCode(errorCode);
    }

    protected void stopEmv() {
        LogUtil.d(TAG, "stopEmv errorCode=[" + getEmvTransData().getErrorCode() + "]");
        // TODO
    }
}
