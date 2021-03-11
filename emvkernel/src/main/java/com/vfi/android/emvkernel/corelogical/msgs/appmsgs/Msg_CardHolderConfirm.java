package com.vfi.android.emvkernel.corelogical.msgs.appmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.MessageType;
import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

public class Msg_CardHolderConfirm extends ToEmvMessage {
    private boolean isCancelled;

    public Msg_CardHolderConfirm(boolean isCancelled) {
        super(MessageType.MSG_CARDHOLDER_CONFIRM);
        this.isCancelled = isCancelled;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
