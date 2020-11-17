package com.vfi.android.emvkernel.corelogical.states.common;

import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartEmv;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartSelectApp;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.libtools.utils.LogUtil;

import static com.vfi.android.emvkernel.corelogical.states.base.EmvStateType.*;

public class IdleState extends AbstractEmvState {
    public IdleState() {
        super(STATE_IDLE);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        LogUtil.d(TAG, "IdleState msgType=" + context.getMessage().getMessageType());
        if (context.getMessage() instanceof Msg_StartEmv) {
            context.getEmvComm().powerOnCardReader();
            jumpToState(STATE_SELECT_APP);
            sendMessage(new Msg_StartSelectApp());
        }
    }
}
