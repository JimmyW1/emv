package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.*;

public class Msg_StartEmv extends ToEmvMessage {
    public Msg_StartEmv() {
        super(MSG_START_EMV);
    }
}
