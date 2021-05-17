package com.vfi.android.emvkernel.corelogical;

import com.vfi.android.emvkernel.corelogical.msgs.appmsgs.Msg_CardHolderConfirm;
import com.vfi.android.emvkernel.corelogical.msgs.appmsgs.Msg_CardHolderSelectFinished;
import com.vfi.android.emvkernel.corelogical.msgs.appmsgs.Msg_InputPinFinished;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartEmv;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.BaseEmvFlow;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.IEmvState;
import com.vfi.android.emvkernel.corelogical.states.common.IdleState;
import com.vfi.android.emvkernel.corelogical.states.common.StopState;
import com.vfi.android.emvkernel.corelogical.states.contact.CardConfirmState;
import com.vfi.android.emvkernel.corelogical.states.contact.CardHolderVerificationState;
import com.vfi.android.emvkernel.corelogical.states.contact.OfflineDataAuthenticationState;
import com.vfi.android.emvkernel.corelogical.states.contact.ProcessingRestrictionsState;
import com.vfi.android.emvkernel.corelogical.states.contact.ReadCardState;
import com.vfi.android.emvkernel.corelogical.states.contact.SelectApplicationState;
import com.vfi.android.emvkernel.corelogical.states.contact.TerminalRiskManagementState;
import com.vfi.android.emvkernel.data.beans.AppInfo;
import com.vfi.android.emvkernel.data.beans.EmvApplication;
import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.emvkernel.interfaces.IEmvOperation;
import com.vfi.android.libtools.utils.LogUtil;

import java.util.List;

import static com.vfi.android.emvkernel.corelogical.states.base.EmvStateType.*;

public class ContactEmvFlow extends BaseEmvFlow implements IEmvOperation {
    public ContactEmvFlow(EmvContext emvContext) {
        super(emvContext);
    }

    @Override
    public void jumpToState(String stateType) {
        IEmvState emvState = null;

        LogUtil.d(TAG, "Current state=[" + ((AbstractEmvState)getCurrentEmvState()).getStateType() + "] ---> State=[" + stateType + "]");

        switch (stateType) {
            case STATE_IDLE:
                emvState = new IdleState();
                break;
            case STATE_SELECT_APP:
                emvState = new SelectApplicationState();
                break;
            case STATE_STOP:
                emvState = new StopState();
                break;
            case STATE_READ_CARD:
                emvState = new ReadCardState();
                break;
            case STATE_CARD_CONFIRM:
                emvState = new CardConfirmState();
                break;
            case STATE_OFFLINE_DATA_AUTHENTICATION:
                emvState = new OfflineDataAuthenticationState();
                break;
            case STATE_PROCESSING_RESTRICTIONS:
                emvState = new ProcessingRestrictionsState();
                break;
            case STATE_CARDHOLDER_VERIFICATION:
                emvState = new CardHolderVerificationState();
                break;
            case STATE_TERMINAL_RISK_MANAGEMENT:
                emvState = new TerminalRiskManagementState();
                break;
        }

        if (emvState != null) {
            setCurrentEmvState(emvState);
        } else {
            LogUtil.d(TAG, "State[" + stateType + "] not found.");
        }
    }

    @Override
    public int initEmvFlow(EmvParams emvParams) {
        getEmvContext().setEmvParams(emvParams);
        jumpToState(STATE_IDLE);
        return 0;
    }

    @Override
    public void startEMVFlow(IEmvHandler emvHandler) {
        setEmvHandler(emvHandler);
        sendMessage(new Msg_StartEmv());
    }

    @Override
    public void stopEmvFlow() {
        jumpToState(STATE_IDLE);
    }

    @Override
    public void importSelectApplication(boolean isCancelled, AppInfo appInfo) {
        for (EmvApplication emvApplication : getEmvContext().getCurrentTransData().getCandidateList()) {
            if (emvApplication.getDfName().equals(appInfo.getAid())) {
                sendMessage(new Msg_CardHolderSelectFinished(isCancelled, emvApplication));
            }
        }
    }

    @Override
    public void importCardConfirmResult(boolean pass) {
        sendMessage(new Msg_CardHolderConfirm(!pass));
    }

    @Override
    public void importPin(int option, byte[] pin) {
        sendMessage(new Msg_InputPinFinished(option == 0, pin));
    }
}
