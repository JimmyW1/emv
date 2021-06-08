package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartTerminalActionAnalysis;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.libtools.utils.LogUtil;

public class TerminalActionAnalysisState extends AbstractEmvState {
    public TerminalActionAnalysisState() {
        super(EmvStateType.STATE_TERMINAL_ACTION_ANALYSIS);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        Message message = context.getMessage();
        LogUtil.d(TAG, "TerminalActionAnalysisState msgType=" + message.getMessageType());
        if (message instanceof Msg_StartTerminalActionAnalysis) {

        }
    }
}
