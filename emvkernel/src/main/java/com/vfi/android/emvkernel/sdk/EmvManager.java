package com.vfi.android.emvkernel.sdk;

import com.vfi.android.emvkernel.corelogical.CTLSPreEmvFlow;
import com.vfi.android.emvkernel.corelogical.ContactEmvFlow;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.data.beans.AppInfo;
import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.interfaces.IEmvComm;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.emvkernel.interfaces.IEmvOperation;

import java.util.List;

public final class EmvManager {
    private static IEmvOperation iEmvOperation;
    private EmvContext emvContext;
    private Thread emvThread;

    private static class SingletonHolder {
        private static final EmvManager INSTANCE = new EmvManager();
    }

    private EmvManager() {
    }

    public static EmvManager getInstance(IEmvComm iEmvComm) {
        EmvManager emvManager = SingletonHolder.INSTANCE;

        EmvContext emvContext = new EmvContext();
        emvContext.setDbOperation(SdkManager.getInstance().getDbManager());
        emvContext.setEmvComm(iEmvComm);

        emvManager.setEmvContext(emvContext);
        iEmvOperation = new ContactEmvFlow(emvContext); // default;
        return emvManager;
    }

    public int initEmvFlow(EmvParams emvParams) {
        if (emvParams.isContact()) {
            iEmvOperation = new ContactEmvFlow(emvContext);
        } else {
            iEmvOperation = new CTLSPreEmvFlow(emvContext);
        }

        return iEmvOperation.initEmvFlow(emvParams);
    }

    public void startEMVFlow(IEmvHandler emvHandler) {
        iEmvOperation.startEMVFlow(emvHandler);
    }

    public void stopEmvFlow() {
        iEmvOperation.stopEmvFlow();
    }

    public void importSelectApplication(boolean isCancelled, AppInfo appInfo) {
        iEmvOperation.importSelectApplication(isCancelled, appInfo);
    }

    public void setEmvTag(String tag, String value) {
        iEmvOperation.setEmvTag(tag, value);
    }

    public String getEmvTag(String tag) {
        return iEmvOperation.getEmvTag(tag);
    }

    public String getEmvTags(List<String> tagList) {
        return iEmvOperation.getEmvTags(tagList);
    }

    public void saveEmvAppParameters(int groupId, String emvAppTagList) {

    }

    public List<String> getEmvAppParameters(int groupId) {
        return null;
    }

    public void saveEmvCapkParameters(int groupId, String emvCapkList) {

    }

    public List<String> getEmvCapkParameters(int groupId) {
        return null;
    }

    public void setEmvContext(EmvContext emvContext) {
        this.emvContext = emvContext;
    }
}
