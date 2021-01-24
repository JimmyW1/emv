package com.vfi.android.emvkernel.corelogical.msgs.emvmsgs;

import com.vfi.android.emvkernel.corelogical.msgs.base.ToEmvMessage;

import static com.vfi.android.emvkernel.corelogical.msgs.base.MessageType.MSG_RESELECT_APP_FROM_CANDIDATE_LIST;

public class Msg_ReSelectAppFromCandidateList extends ToEmvMessage {
    public Msg_ReSelectAppFromCandidateList() {
        super(MSG_RESELECT_APP_FROM_CANDIDATE_LIST);
    }
}
