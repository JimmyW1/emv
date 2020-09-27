package com.vfi.android.emvkernel.sdk;

import com.vfi.android.emvkernel.corelogical.CTLSPreEmvFlow;
import com.vfi.android.emvkernel.corelogical.ContactEmvFlow;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.database.DbManager;
import com.vfi.android.emvkernel.interfaces.IEmvComm;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.emvkernel.interfaces.IEmvOperation;

import java.util.List;

public final class EmvManager implements IEmvOperation {
    private static IEmvOperation iEmvOperation;

    private static class SingletonHolder {
        private static final EmvManager INSTANCE = new EmvManager();
    }

    private EmvManager() {
    }

    public static EmvManager getInstance(boolean isContact, IEmvComm iEmvComm) {
        EmvContext emvContext = new EmvContext();
        emvContext.setDbOperation(new DbManager());
        emvContext.setEmvComm(iEmvComm);
        if (isContact) {
            iEmvOperation = new ContactEmvFlow(emvContext);
        } else {
            iEmvOperation = new CTLSPreEmvFlow(emvContext);
        }

        return SingletonHolder.INSTANCE;
    }

    @Override
    public int initEmvFlow(EmvParams emvParams) {
        return iEmvOperation.initEmvFlow(emvParams);
    }

    @Override
    public void startEMVFlow(IEmvHandler emvHandler) {
        iEmvOperation.startEMVFlow(emvHandler);
    }

    @Override
    public void stopEmvFlow() {
        iEmvOperation.stopEmvFlow();
    }

    @Override
    public void setEmvTag(String tag, String value) {
        iEmvOperation.setEmvTag(tag, value);
    }

    @Override
    public String getEmvTag(String tag) {
        return iEmvOperation.getEmvTag(tag);
    }

    @Override
    public String getEmvTags(List<String> tagList) {
        return iEmvOperation.getEmvTags(tagList);
    }
}
