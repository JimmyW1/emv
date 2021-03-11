package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.MSG_START_CARD_CONFIRM;


public class Msg_StartCardConfirm extends ToEmvMessage {
    public Msg_StartCardConfirm() {
        super(MSG_START_CARD_CONFIRM);
    }
}
