package com.vfi.android.emvkernel.corelogical.msgs.appmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.MessageType;
import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

public class Msg_CardHolderSelectFinished extends ToEmvMessage {
    private boolean isCancelled;
    private String selectedDfName;

    public Msg_CardHolderSelectFinished(boolean isCancelled, String selectedDfName) {
        super(MessageType.MSG_CARDHOLDER_SELECT_FINISHED);
        this.isCancelled = isCancelled;
        this.selectedDfName = selectedDfName;
    }

    public String getSelectedDfName() {
        return selectedDfName;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
