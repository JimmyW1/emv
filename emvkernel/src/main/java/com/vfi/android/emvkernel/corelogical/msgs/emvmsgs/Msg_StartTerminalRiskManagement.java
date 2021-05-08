package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.MSG_START_TERMINAL_RISK_MANAGEMENT;


public class Msg_StartTerminalRiskManagement extends ToEmvMessage {
    public Msg_StartTerminalRiskManagement() {
        super(MSG_START_TERMINAL_RISK_MANAGEMENT);
    }
}
