package com.vfi.android.emvkernel.corelogical.msgs.appmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.MessageType;
import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

public class Msg_InputPinFinished extends ToEmvMessage {
    private boolean isCancelled;
    private byte[] pin;

    public Msg_InputPinFinished(boolean isCancelled, byte[] pin) {
        super(MessageType.MSG_INPUT_PIN_FINISHED);
        this.isCancelled = isCancelled;
        this.pin = pin;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public byte[] getPin() {
        return pin;
    }
}
