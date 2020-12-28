package com.vfi.android.emvkernel.corelogical;

import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.states.base.BaseEmvFlow;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.interfaces.IEmvComm;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.emvkernel.interfaces.IEmvOperation;

import java.util.List;

public class CTLSPreEmvFlow extends BaseEmvFlow implements IEmvOperation {
    public CTLSPreEmvFlow(EmvContext emvContext) {
        super(emvContext);
    }

    @Override
    public void jumpToState(String stateType) {

    }

    @Override
    public int initEmvFlow(EmvParams emvParams) {
        getEmvContext().setEmvParams(emvParams);
        return 0;
    }

    @Override
    public void startEMVFlow(IEmvHandler emvHandler) {

    }

    @Override
    public void stopEmvFlow() {

    }
}
