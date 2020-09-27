package com.vfi.android.emvkernel.corelogical.states.base;

import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.database.IDbOperation;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.emvkernel.interfaces.IEmvComm;

public class EmvContext {
    private IEmvComm emvComm;
    private IEmvHandler emvHandler;
    private BaseEmvFlow baseEmvFlow;
    private IDbOperation dbOperation;
    private EmvParams emvParams;

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
        return emvParams;
    }

    public void setEmvParams(EmvParams emvParams) {
        this.emvParams = emvParams;
    }
}
