package com.vfi.android.emvkernel.corelogical.msgs.appmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.MessageType;
import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;
import com.vfi.android.emvkernel.data.beans.EmvApplication;

public class Msg_CardHolderSelectFinished extends ToEmvMessage {
    private boolean isCancelled;
    private EmvApplication emvApplication;

    public Msg_CardHolderSelectFinished(boolean isCancelled, EmvApplication emvApplication) {
        super(MessageType.MSG_CARDHOLDER_SELECT_FINISHED);
        this.isCancelled = isCancelled;
        this.emvApplication = emvApplication;
    }

    public EmvApplication getEmvApplication() {
        return emvApplication;
    }

    public String getSelectedDfName() {
        return emvApplication.getDfName();
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
