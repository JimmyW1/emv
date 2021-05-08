package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.GetDataCmd;
import com.vfi.android.emvkernel.corelogical.apdu.GetDataResponse;
import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartCardHolderVerification;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartTerminalRiskManagement;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.beans.tagbeans.CvmList;
import com.vfi.android.emvkernel.data.beans.tagbeans.CvmRule;
import com.vfi.android.emvkernel.data.beans.tagbeans.TSI;
import com.vfi.android.emvkernel.data.beans.tagbeans.TVR;
import com.vfi.android.emvkernel.data.beans.tagbeans.TerminalCapabilities;
import com.vfi.android.emvkernel.data.beans.tagbeans.TerminalType;
import com.vfi.android.emvkernel.data.consts.CvmType;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.emvkernel.data.consts.TransType;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

public class CardHolderVerificationState extends AbstractEmvState {
    private int currentCvmRuleIndex;
    private CvmList cvmList;
    private int pinTryCounter;

    public CardHolderVerificationState() {
        super(EmvStateType.STATE_CARDHOLDER_VERIFICATION);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        Message message = context.getMessage();
        LogUtil.d(TAG, "CardHolderVerificationState msgType=" + message.getMessageType());

        if (message instanceof Msg_StartCardHolderVerification) {
            pinTryCounter = -1;
            processStartCardHolderVerificationMessage(message);
        }
    }

    private void processStartCardHolderVerificationMessage(Message message) {
        //If the CVM List is not present in the ICC, the terminal shall terminate
        //cardholder verification without setting the ‘Cardholder verification was
        //performed’ bit in the TSI
        if (!getEmvTransData().getTagMap().containsKey(EMVTag.tag8E)) {
            LogUtil.d(TAG, "NO CVM List present");
            jumpToState(EmvStateType.STATE_TERMINAL_RISK_MANAGEMENT);
            sendMessage(new Msg_StartTerminalRiskManagement());
            return;
        }

        String iccCvmList = getEmvTransData().getTagMap().get(EMVTag.tag8E);
        if (iccCvmList.length() <= 16) {
            // Note: A CVM List with no Cardholder Verification Rules is considered to be the same as
            //a CVM List not being present
            LogUtil.d(TAG, "NO Card holder verification rules present");
            jumpToState(EmvStateType.STATE_TERMINAL_RISK_MANAGEMENT);
            sendMessage(new Msg_StartTerminalRiskManagement());
            return;
        }

        currentCvmRuleIndex = 0;
        doCvmVerification();
    }

    private void doCvmVerification() {
        LogUtil.d(TAG, "doCvmVerification");
        TerminalCapabilities terminalCap = new TerminalCapabilities(getEmvTransData().getTagMap().get(TerminalTag.tag9F33));

        if (cvmList == null) {
            String iccCvmList = getEmvTransData().getTagMap().get(EMVTag.tag8E);
            cvmList = new CvmList(iccCvmList);
        }

        for (int i = currentCvmRuleIndex; i < cvmList.getCvmRules().size(); i++) {
            if (isMatchCondition(terminalCap, cvmList.getCvmRules().get(i))) {
                requestPerformCVM(cvmList.getCvmRules().get(i));
                return;
            }
        }

        // no cvm matched, perform cvm failed.
        processCvmVerificationResult(false);
    }

    private int getPINTryCounter() {
        if (pinTryCounter == -1) {
            byte[] result = executeApduCmd(new GetDataCmd(GetDataCmd.TYPE_PIN_TRY_COUNTER));
            GetDataResponse response = new GetDataResponse(result);
            if (response.isSuccess()) {
                response.saveTags(getEmvTransData().getTagMap());
                String pinTryCounterHex = response.getTlvMap().get(EMVTag.tag9F17);
                pinTryCounter = StringUtil.parseInt(pinTryCounterHex, 16, 0);
                LogUtil.d(TAG, "Get data pinTryCounter=[" + pinTryCounter + "]");
            } else {
                LogUtil.d(TAG, "Get Data failed.");
                pinTryCounter = 0;
            }
        } else {
            pinTryCounter--;
        }

        LogUtil.d(TAG, "pinTryCounter=[" + pinTryCounter + "]");
        return pinTryCounter;
    }

    private void requestPerformCVM(CvmRule cvmRule) {
        byte cvmType = (byte) (cvmRule.getCvmCode() & 0x3F);
        switch (cvmType) {
            case CvmType.PLAINTEXT_PIN_VERIFICATION_ICC:
            case CvmType.PLAINTEXT_PIN_VERIFICATION_ICC_AND_SIGNATURE:
            case CvmType.ENCIPHERED_PIN_VERIFICATION_ICC:
            case CvmType.ENCIPHERED_PIN_VERIFICATION_ICC_AND_SIGNATURE:
                getEmvHandler().onRequestOfflinePIN(getPINTryCounter());
                break;
            case CvmType.ENCIPHERED_PIN_VERIFIED_ONLINE:
                getEmvHandler().onRequestOnlinePIN();
                break;
            case CvmType.SIGNATURE:
            case CvmType.NO_CVM_REQUIRED:
                // TODO set cvm result
                processCvmVerificationResult(true);
                break;
        }
    }

    private void processCvmVerificationResult(boolean isSuccess) {
        if (!isSuccess) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_CARDHOLDER_VERIFICATION_WAS_NOT_SUCCESSFUL, true);
        }

        getEmvTransData().getTsi().markFlag(TSI.FLAG_CARDHOLDER_VERIFICATION_WAS_PERFORMED, true);
        jumpToState(EmvStateType.STATE_TERMINAL_RISK_MANAGEMENT);
        sendMessage(new Msg_StartTerminalRiskManagement());
    }

    private boolean isAttendedTerminalType() {
        TerminalType terminalType = new TerminalType(getEmvTransData().getTagMap().get(TerminalTag.tag9F35));
        return terminalType.isAttendedTerminalType();
    }

    private String getTransType() {
        return getEmvContext().getEmvParams().getTransProcessCode();
    }

    private boolean isAppCurrencyCodeEqualToTransCurrencyCode() {
        if (!getEmvTransData().getTagMap().containsKey(EMVTag.tag9F42)) {
            // data required by the condition (for example, the Application Currency Code or
            //Amount, Authorised) is not present, then the terminal shall bypass the rule and proceed to the next.
            LogUtil.d(TAG, "Application Currency Code Not found.");
            return false;
        }
        String applicationCurrencyCode = getEmvTransData().getTagMap().get(EMVTag.tag9F42);

        String transactionCurrencyCode = getEmvContext().getEmvParams().getTransCurrencyCode();
        if (transactionCurrencyCode == null || transactionCurrencyCode.length() == 0) {
            LogUtil.d(TAG, "transactionCurrencyCode not set in EmvParams");
            if (!getEmvTransData().getTagMap().containsKey(TerminalTag.tag5F2A)) {
                LogUtil.d(TAG, "Transaction Currency Code Not found.");
                return false;
            }

            transactionCurrencyCode = getEmvTransData().getTagMap().get(TerminalTag.tag5F2A);
        }

        LogUtil.d(TAG, "applicationCurrencyCode=[" + applicationCurrencyCode + "] transactionCurrencyCode=[" + transactionCurrencyCode + "]");
        if (applicationCurrencyCode.equals(transactionCurrencyCode)) {
            return true;
        }

        return false;
    }

    private boolean isTransAmountUnder(String amount) {
        String transactionAmount = getEmvTransData().getTagMap().get(TerminalTag.tag9F02);
        long transAmountLong = StringUtil.parseLong(transactionAmount, 0);
        long amountLong = StringUtil.parseLong(amount, 0);

        boolean isTransAmountUnder = (transAmountLong <= amountLong);
        LogUtil.d(TAG, "isTransAmountUnder=[" + isTransAmountUnder + "]");
        return isTransAmountUnder;
    }

    private boolean isMatchCondition(TerminalCapabilities terminalCap, CvmRule cvmRule) {
        byte cvmCondition = cvmRule.getCvmConditionCode();
        String transType = getTransType();
        boolean isAttendedTerminalType = isAttendedTerminalType();
        boolean isAppCurrencyCodeEqualToTransCurrencyCode = isAppCurrencyCodeEqualToTransCurrencyCode();

        switch (cvmCondition) {
            case 0x00: // Always
                break;
            case 0x01: // If unattended cash
                if (isAttendedTerminalType) {
                    return false;
                }
                break;
            case 0x02: // If not unattended cash and not manual cash and not purchase with cashback
                if (transType.equals(TransType.CASH) || !transType.equals(TransType.CASH_BACK)) {
                    return false;
                }
                break;
            case 0x03: // If terminal supports the CVM
                break;
            case 0x04: // If manual cash
                if (!(isAttendedTerminalType && transType.equals(TransType.CASH))) {
                    return false;
                }
                break;
            case 0x05: // If purchase with cashback
                if (!transType.equals(TransType.CASH)) {
                    return false;
                }
                break;
            case 0x06: // If transaction is in the application currency and is under X value (see section 10.5 for a discussion of “X”)
                if (!(isAppCurrencyCodeEqualToTransCurrencyCode && isTransAmountUnder(cvmList.getAmountX()))) {
                    return false;
                }
                break;
            case 0x07: // If transaction is in the application currency and is over X value
                if (!(isAppCurrencyCodeEqualToTransCurrencyCode && !isTransAmountUnder(cvmList.getAmountX()))) {
                    return false;
                }
                break;
            case 0x08: // If transaction is in the application currency and is under Y value (see section 10.5 for a discussion of “Y”)
                if (!(isAppCurrencyCodeEqualToTransCurrencyCode && isTransAmountUnder(cvmList.getAmountY()))) {
                    return false;
                }
                break;
            case 0x09: // If transaction is in the application currency and is over Y value
                if (!(isAppCurrencyCodeEqualToTransCurrencyCode && !isTransAmountUnder(cvmList.getAmountY()))) {
                    return false;
                }
                break;
            default:
                // the CVM Condition Code is outside the range of codes understood by the
                //terminal (which might occur if the terminal application program is at a
                //different version level than the ICC application),
                // then the terminal shall bypass the rule and proceed to the next
                return false;
        }

        // application currency = That is, Transaction Currency Code = Application Currency Code.

        byte cvmType = (byte) (cvmRule.getCvmCode() & 0x3F);
        switch (cvmType) {
            case CvmType.PLAINTEXT_PIN_VERIFICATION_ICC:
                if (terminalCap.isSupportPlainTextPinVerifyByICC()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] PLAINTEXT_PIN_VERIFICATION_ICC");
                    return true;
                }
                break;
            case CvmType.ENCIPHERED_PIN_VERIFIED_ONLINE:
                if (terminalCap.isSupportEncipheredPINForOnlineVerification()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] ENCIPHERED_PIN_VERIFIED_ONLINE");
                    return true;
                }
                break;
            case CvmType.PLAINTEXT_PIN_VERIFICATION_ICC_AND_SIGNATURE:
                if (terminalCap.isSupportPlainTextPinVerifyByICC() && terminalCap.isSupportSignaturePaper()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] PLAINTEXT_PIN_VERIFICATION_ICC_AND_SIGNATURE");
                    return true;
                }
                break;
            case CvmType.ENCIPHERED_PIN_VERIFICATION_ICC:
                if (terminalCap.isSupportEncipheredPINForOfflineVerification()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] ENCIPHERED_PIN_VERIFICATION_ICC");
                    return true;
                }
                break;
            case CvmType.ENCIPHERED_PIN_VERIFICATION_ICC_AND_SIGNATURE:
                if (terminalCap.isSupportEncipheredPINForOfflineVerification() && terminalCap.isSupportSignaturePaper()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] ENCIPHERED_PIN_VERIFICATION_ICC_AND_SIGNATURE");
                    return true;
                }
                break;
            case CvmType.SIGNATURE:
                if (terminalCap.isSupportSignaturePaper()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] SIGNATURE");
                    return true;
                }
                break;
            case CvmType.NO_CVM_REQUIRED:
                if (terminalCap.isSupportNoCVM()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] NO_CVM_REQUIRED");
                    return true;
                }
                break;
        }
        return false;
    }
}
