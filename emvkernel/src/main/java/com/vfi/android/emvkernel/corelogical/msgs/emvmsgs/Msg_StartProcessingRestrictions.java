package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.MSG_START_PROCESSING_RESTRICTIONS;


public class Msg_StartProcessingRestrictions extends ToEmvMessage {
    public Msg_StartProcessingRestrictions() {
        super(MSG_START_PROCESSING_RESTRICTIONS);
    }
}
