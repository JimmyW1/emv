package com.vfi.android.emvkernel.corelogical.states.common;

import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StopEmv;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.libtools.utils.LogUtil;

import static com.vfi.android.emvkernel.corelogical.states.base.EmvStateType.STATE_STOP;

public class StopState extends AbstractEmvState {
    public StopState() {
        super(STATE_STOP);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        LogUtil.d(TAG, "IdleState msgType=" + context.getMessage().getMessageType());
        if (context.getMessage() instanceof Msg_StopEmv) {
            // TODO
        }
    }
}
