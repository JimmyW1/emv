package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.MSG_START_TERMINAL_ACTION_ANALYSIS;


public class Msg_StartTerminalActionAnalysis extends ToEmvMessage {
    public Msg_StartTerminalActionAnalysis() {
        super(MSG_START_TERMINAL_ACTION_ANALYSIS);
    }
}
