package com.vfi.android.emvkernel.corelogical.states.base;

import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StopEmv;
import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.emvkernel.data.beans.EmvApplication;
import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.data.beans.EmvTransData;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.emvkernel.interfaces.IEmvHandler;
import com.vfi.android.emvkernel.utils.SecurityUtil;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractEmvState implements IEmvState {
    protected final String TAG = TAGS.EMV_STATE;
    private EmvContext emvContext;
    private String stateType;

    public AbstractEmvState(String stateType) {
        this.stateType = stateType;
    }

    protected void jumpToState(String stateType) {
        if (emvContext != null && emvContext.getBaseEmvFlow() != null) {
            emvContext.getBaseEmvFlow().jumpToState(stateType);
        }
    }

    public String getStateType() {
        return stateType;
    }

    protected byte[] executeApduCmd(ApduCmd apduCmd) {
        if (emvContext != null && emvContext.getEmvComm() != null) {
            return emvContext.getEmvComm().executeApduCmd(apduCmd);
        }

        return null;
    }

    protected void sendMessage(Message message) {
        if (emvContext != null && emvContext.getBaseEmvFlow() != null) {
            emvContext.getBaseEmvFlow().sendMessage(message);
        }
    }

    @Override
    public void run(EmvContext context) {
        this.emvContext = context;
    }

    public EmvContext getEmvContext() {
        return emvContext;
    }

    public EmvTransData getEmvTransData() {
        return emvContext.getCurrentTransData();
    }

    public void initializeTerminalTags() {
        Map<String, String> tagMap = getEmvTransData().getTagMap();
        EmvParams emvParams = getEmvContext().getEmvParams();

        if (emvParams.isSupportECash()) {
            tagMap.put(TerminalTag.tag9F7A, "01");
        } else {
            tagMap.put(TerminalTag.tag9F7A, "00");
        }

        String amount = emvParams.getAmount();
        if (amount != null && amount.length() > 0) {
            tagMap.put(TerminalTag.tag9F02, StringUtil.getNonNullStringLeftPadding(amount, 12));
        } else {
            tagMap.put(TerminalTag.tag9F02, "000000000000");
        }

        String transactionType = emvParams.getTransProcessCode();
        transactionType = StringUtil.getNonNullStringRightPadding(transactionType, 2);
        tagMap.put(TerminalTag.tag9C, transactionType);

        String terminalCountryCode = emvParams.getTerminalCountryCode();
        if (terminalCountryCode != null && terminalCountryCode.length() > 0) {
            terminalCountryCode = StringUtil.getNonNullStringLeftPadding(terminalCountryCode, 4);
            tagMap.put(TerminalTag.tag9F1A, terminalCountryCode);
        }

        // put selected terminal TAGS to current tag Map
        Map<String, String> selectAppTerminalParamsMap = getEmvTransData().getSelectAppTerminalParamsMap();
        List<String> terminalTagList = Arrays.asList(TerminalTag.tag9F33, TerminalTag.tag9F09, TerminalTag.tag9F1A, TerminalTag.tag5F2A, TerminalTag.tag9F35);
        for (String tag : terminalTagList) {
            if (selectAppTerminalParamsMap.containsKey(tag)) {
                tagMap.put(tag, selectAppTerminalParamsMap.get(tag));
            }
        }

        // if emv param set trans currency code, will use this value, if not set will use Application Parameter value
        String transCurrencyCode = emvParams.getTransCurrencyCode();
        // TODO after select application use emv parameter transaction currency code replace this one.
        if (transCurrencyCode != null && transCurrencyCode.length() > 0) {
            tagMap.put(TerminalTag.tag5F2A, StringUtil.getNonNullStringLeftPadding(transCurrencyCode, 4));
        }

        String unpredictableNumber = SecurityUtil.getRandomBytesAndBreakDown(4);
        LogUtil.d(TAG, "TAG9F37(Unpredictable Number)=[" + unpredictableNumber + "]");
        tagMap.put(TerminalTag.tag9F37, unpredictableNumber);
    }

    public IEmvHandler getEmvHandler() {
        return emvContext.getEmvHandler();
    }

    public void addCandidateApplication(EmvApplication emvApplication) {
        if (isDFNameExist(emvApplication.getDfName())) {
            LogUtil.d(TAG, "Skip tag4F[" + emvApplication.getDfName() + "] add to candidate list, exist in candidate now");
        } else {
            LogUtil.d(TAG, "tag4F[" + emvApplication.getDfName() + "] add to candidate list.");
            getEmvTransData().getCandidateList().add(emvApplication);
        }
    }

    public void removeCandidateApplication(String dfName) {
        Iterator<EmvApplication> iterator = getEmvTransData().getCandidateList().iterator();
        while (iterator.hasNext()) {
            EmvApplication emvApplication = iterator.next();
            if (emvApplication.getDfName().equals(dfName)) {
                iterator.remove();
                break;
            }
        }
    }

    public void clearCandidateList() {
        LogUtil.d(TAG, "clearCandidateList");
        getEmvTransData().getCandidateList().clear();
    }

    private boolean isDFNameExist(String dfName) {
        for (EmvApplication emvApplication : getEmvTransData().getCandidateList()) {
            if (dfName.equals(emvApplication.getDfName())) {
                return true;
            }
        }

        return false;
    }

    protected void setErrorCode(int errorCode) {
        getEmvTransData().setErrorCode(errorCode);
    }

    protected void stopEmv() {
        LogUtil.d(TAG, "stopEmv errorCode=[" + getEmvTransData().getErrorCode() + "]");
        jumpToState(EmvStateType.STATE_STOP);
        sendMessage(new Msg_StopEmv());
    }
}
