package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.MSG_STOP_EMV;

public class Msg_StopEmv extends ToEmvMessage {
    private int errorCode;

    public static final int ERR_SELECT_APP_FAILED = 1;

    public Msg_StopEmv(int errorCode) {
        super(MSG_STOP_EMV);

        this.errorCode = errorCode;
    }
}
