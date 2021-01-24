package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.MSG_STOP_EMV;

public class Msg_StopEmv extends ToEmvMessage {
    public Msg_StopEmv() {
        super(MSG_STOP_EMV);
    }
}
