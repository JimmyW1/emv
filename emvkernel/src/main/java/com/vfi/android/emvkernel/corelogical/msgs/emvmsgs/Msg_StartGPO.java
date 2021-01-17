package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.MSG_START_GPO;

public class Msg_StartGPO extends ToEmvMessage {
    public Msg_StartGPO() {
        super(MSG_START_GPO);
    }
}
