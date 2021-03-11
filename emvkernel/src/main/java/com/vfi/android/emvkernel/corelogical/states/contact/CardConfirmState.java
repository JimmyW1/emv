package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.msgs.appmsgs.Msg_CardHolderConfirm;
import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartCardConfirm;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartOfflineDataAuth;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.beans.CardInfo;
import com.vfi.android.emvkernel.data.consts.EMVResultCode;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.libtools.utils.LogUtil;

import java.util.Map;

public class CardConfirmState extends AbstractEmvState {
    public CardConfirmState() {
        super(EmvStateType.STATE_CARD_CONFIRM);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        Message message = context.getMessage();
        LogUtil.d(TAG, "CardConfirmState msgType=" + message.getMessageType());
        if (message instanceof Msg_StartCardConfirm) {
            processStartCardConfirmMessage(message);
        } else if (message instanceof Msg_CardHolderConfirm) {
            processCardHolderConfirmMessage(message);
        }
    }

    private void processStartCardConfirmMessage(Message message) {
        Map<String, String> tagMap = getEmvTransData().getTagMap();

        CardInfo cardInfo = new CardInfo();
        cardInfo.setPan(tagMap.get(EMVTag.tag5A));
        cardInfo.setTrack1(tagMap.get(EMVTag.tag9F1F));
        cardInfo.setTrack2(tagMap.get(EMVTag.tag57));
        cardInfo.setCardSequenceNum(tagMap.get(EMVTag.tag5F34));
        getEmvHandler().onConfirmCardInfo(cardInfo);
    }

    private void processCardHolderConfirmMessage(Message message) {
        Msg_CardHolderConfirm msg = (Msg_CardHolderConfirm) message;
        if (msg.isCancelled()) {
            setErrorCode(EMVResultCode.ERR_CARD_HOLDER_CANCELLED);
            stopEmv();
        } else {
            jumpToState(EmvStateType.STATE_OFFLINE_DATA_AUTHENTICATION);
            sendMessage(new Msg_StartOfflineDataAuth());
        }
    }
}
