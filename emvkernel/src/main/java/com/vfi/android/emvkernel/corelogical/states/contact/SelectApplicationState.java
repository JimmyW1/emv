package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectCmd;
import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectResponse;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartSelectApp;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StopEmv;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.data.beans.ApduResponse;
import com.vfi.android.libtools.utils.LogUtil;

import static com.vfi.android.emvkernel.corelogical.states.base.EmvStateType.*;

public class SelectApplicationState extends AbstractEmvState {
    public SelectApplicationState() {
        super(STATE_SELECT_APP);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);
        LogUtil.d(TAG, "SelectApplicationState msgType=" + context.getMessage().getMessageType());
        if (context.getMessage() instanceof Msg_StartSelectApp) {
            byte[] retData = executeApduCmd(new ApplicationSelectCmd(true, true, "1PAY.SYS.DDF01"));
            ApplicationSelectResponse response = new ApplicationSelectResponse(retData);
            if (!response.isSuccess()) {
                sendMessage(new Msg_StopEmv(Msg_StopEmv.ERR_SELECT_APP_FAILED));
                jumpToState(STATE_STOP);
                return;
            }


        }
    }
}
