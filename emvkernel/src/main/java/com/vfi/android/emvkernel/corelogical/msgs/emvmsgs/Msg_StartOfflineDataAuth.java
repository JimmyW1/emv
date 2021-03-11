package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.MSG_START_OFFLINE_DATA_AUTH;


public class Msg_StartOfflineDataAuth extends ToEmvMessage {
    public Msg_StartOfflineDataAuth() {
        super(MSG_START_OFFLINE_DATA_AUTH);
    }
}
