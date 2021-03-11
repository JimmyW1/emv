package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartOfflineDataAuth;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.libtools.utils.LogUtil;

public class OfflineDataAuthenticationState extends AbstractEmvState {
    public OfflineDataAuthenticationState() {
        super(EmvStateType.STATE_OFFLINE_DATA_AUTHENTICATION);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        Message message = context.getMessage();
        LogUtil.d(TAG, "OfflineDataAuthenticationState msgType=" + message.getMessageType());
        if (message instanceof Msg_StartOfflineDataAuth) {
            processStartOfflineDataAuthMessage(message);
        }
    }

    private void processStartOfflineDataAuthMessage(Message message) {

    }
}
