package com.vfi.android.emvkernel.corelogical;

import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.interfaces.IEMVHandler;
import com.vfi.android.emvkernel.interfaces.IEmvComm;
import com.vfi.android.emvkernel.interfaces.IEmvOperation;

import java.util.List;

public class ContactEmvFlow implements IEmvOperation {
    private IEmvComm iEmvComm;
    private EmvParams emvParams;
    private IEMVHandler emvHandler;

    public ContactEmvFlow(IEmvComm iEmvComm) {
        this.iEmvComm = iEmvComm;
    }

    @Override
    public int initEmvFlow(EmvParams emvParams) {
        this.emvParams = emvParams;

        return 0;
    }

    @Override
    public void startEMVFlow(IEMVHandler emvHandler) {
        this.emvHandler = emvHandler;
    }

    @Override
    public void stopEmvFlow() {

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
