package com.vfi.android.emvkernel.corelogical.states.base;

import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.data.beans.EmvTransData;
import com.vfi.android.emvkernel.database.IDbOperation;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.emvkernel.interfaces.IEmvComm;

public class EmvContext {
    private IEmvComm emvComm;
    private IEmvHandler emvHandler;
    private BaseEmvFlow baseEmvFlow;
    private IDbOperation dbOperation;
    private EmvParams emvParams;
    private Message message;

    private EmvTransData currentTransData;

    public EmvContext() {
    }

    public void setBaseEmvFlow(BaseEmvFlow baseEmvFlow) {
        this.baseEmvFlow = baseEmvFlow;
    }

    public BaseEmvFlow getBaseEmvFlow() {
        return baseEmvFlow;
    }

    public IEmvComm getEmvComm() {
        return emvComm;
    }

    public void setEmvComm(IEmvComm emvComm) {
        this.emvComm = emvComm;
    }

    public void setEmvHandler(IEmvHandler emvHandler) {
        this.emvHandler = emvHandler;
    }

    public IEmvHandler getEmvHandler() {
        return emvHandler;
    }

    public IDbOperation getDbOperation() {
        return dbOperation;
    }

    public void setDbOperation(IDbOperation dbOperation) {
        this.dbOperation = dbOperation;
    }

    public EmvParams getEmvParams() {
        if (emvParams == null) {
            emvParams = new EmvParams();
        }

        return emvParams;
    }

    public void setEmvParams(EmvParams emvParams) {
        this.emvParams = emvParams;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public EmvTransData getCurrentTransData() {
        if (currentTransData == null) {
            currentTransData = new EmvTransData();
        }
        return currentTransData;
    }

    public void setCurrentTransData(EmvTransData currentTransData) {
        this.currentTransData = currentTransData;
    }
}
