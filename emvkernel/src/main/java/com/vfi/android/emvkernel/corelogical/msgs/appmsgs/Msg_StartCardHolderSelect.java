package com.vfi.android.emvkernel.corelogical.msgs.appmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.MessageType;
import com.vfi.android.emvkernel.corelogical.msgs.base.ToAppMessage;
import com.vfi.android.emvkernel.data.beans.EmvApplication;

import java.util.List;

public class Msg_StartCardHolderSelect extends ToAppMessage {
    private List<EmvApplication> orderedEmvApplicationList;

    public Msg_StartCardHolderSelect(List<EmvApplication> orderedEmvApplicationList) {
        super(MessageType.MSG_START_CARDHOLDER_SELECT);
        this.orderedEmvApplicationList = orderedEmvApplicationList;
    }
}
