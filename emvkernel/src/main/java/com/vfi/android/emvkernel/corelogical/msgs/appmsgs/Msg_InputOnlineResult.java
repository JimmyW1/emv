package com.vfi.android.emvkernel.corelogical.msgs.appmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.MessageType;
import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;
import com.vfi.android.emvkernel.data.beans.OnlineResult;

public class Msg_InputOnlineResult extends ToEmvMessage {
    private OnlineResult onlineResult;

    public Msg_InputOnlineResult(OnlineResult onlineResult) {
        super(MessageType.MSG_INPUT_ONLINE_RESULT);
        this.onlineResult = onlineResult;
    }

    public OnlineResult getOnlineResult() {
        return onlineResult;
    }
}
