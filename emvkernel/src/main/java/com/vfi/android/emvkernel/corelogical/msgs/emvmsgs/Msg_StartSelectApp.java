package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.*;

public class Msg_StartSelectApp extends ToEmvMessage {
    public Msg_StartSelectApp() {
        super(MSG_START_SELECT_APP);
    }
}
