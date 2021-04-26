package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.MSG_START_CARDHOLDER_VERIFICATION;


public class Msg_StartCardHolderVerification extends ToEmvMessage {
    public Msg_StartCardHolderVerification() {
        super(MSG_START_CARDHOLDER_VERIFICATION);
    }
}
