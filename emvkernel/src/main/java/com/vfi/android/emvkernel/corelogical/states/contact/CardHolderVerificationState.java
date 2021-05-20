package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.GetDataCmd;
import com.vfi.android.emvkernel.corelogical.apdu.GetDataResponse;
import com.vfi.android.emvkernel.corelogical.apdu.VerifyCmd;
import com.vfi.android.emvkernel.corelogical.apdu.VerifyResponse;
import com.vfi.android.emvkernel.corelogical.msgs.appmsgs.Msg_InputPinFinished;
import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartCardHolderVerification;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartTerminalRiskManagement;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.beans.tagbeans.CvmList;
import com.vfi.android.emvkernel.data.beans.tagbeans.CvmResult;
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

import java.util.Arrays;

public class CardHolderVerificationState extends AbstractEmvState {
    private int currentCvmRuleIndex;
    private CvmList cvmList;
    private int pinTryCounter;
    private boolean isPinBypassedBefore;

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
            isPinBypassedBefore = false;

            if (getEmvTransData().getAIP().isSupportCardHolderVerification()) {
                processStartCardHolderVerificationMessage(message);
            } else {
                //                                                                              Corresponding CVM Results
                //    Conditions                                                        |    Byte 1 (CVM Performed)                  |   Byte 2 (CVM Condition) |  Byte 3 (CVM Result)  |  TVR byte 3 | TSI byte 1 bit 7
                // When the card does not support cardholder verification (AIP bit 5=0) | '3F' (Book 4 Section 6.3.4.5 and Annex A4) | '00' (no meaning)        |    '00'               |    --       |      0
                getEmvTransData().getCvmResult().setCvmResult((byte)0x3F, (byte)0x00, CvmResult.UNKNOWN);
                jumpToState(EmvStateType.STATE_TERMINAL_RISK_MANAGEMENT);
                sendMessage(new Msg_StartTerminalRiskManagement());
            }
        } else if (message instanceof Msg_InputPinFinished) {
            processInputPinFinishedMessage(message);
        }
    }

    private void processStartCardHolderVerificationMessage(Message message) {
        //If the CVM List is not present in the ICC, the terminal shall terminate
        //cardholder verification without setting the ‘Cardholder verification was
        //performed’ bit in the TSI
        if (!getEmvTransData().getTagMap().containsKey(EMVTag.tag8E)) {
            LogUtil.d(TAG, "NO CVM List present");
            getEmvTransData().getCvmResult().setCvmResult((byte)0x3F, (byte)0x00, CvmResult.UNKNOWN);
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

    private void processInputPinFinishedMessage(Message message) {
        Msg_InputPinFinished msg = (Msg_InputPinFinished) message;
        if (msg.isCancelled()) {
            // TODO confirm if need stop emv and how to set tvr tsi flag
            LogUtil.d(TAG, "Input pin cancelled.");
            stopEmv();
            return;
        }

        CvmRule cvmRule = cvmList.getCvmRules().get(currentCvmRuleIndex);
        if (msg.getPin() == null || msg.getPin().length == 0) {
            LogUtil.d(TAG, "Bypass pin happened");
            isPinBypassedBefore = true;
            doPinBypassProcess(cvmRule);
            return;
        }

        byte cvmType = (byte) (cvmRule.getCvmCode() & 0x3F);
        switch (cvmType) {
            case CvmType.PLAINTEXT_PIN_VERIFICATION_ICC:
            case CvmType.PLAINTEXT_PIN_VERIFICATION_ICC_AND_SIGNATURE:
                if (doPlainTextPinVerification(msg.getPin())) {
                    if (cvmType == CvmType.PLAINTEXT_PIN_VERIFICATION_ICC_AND_SIGNATURE) {
                        getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.UNKNOWN);
                    } else {
                        getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.SUCCESSFUL);
                    }
                } else {
                    // failed
                    LogUtil.d(TAG, "Verify plain text offline pin failed. pinTryCounter=[" + pinTryCounter + "]");
                    checkAndPerformRetryOfflinePin(cvmRule);
                    return;
                }
                break;
            case CvmType.ENCIPHERED_PIN_VERIFICATION_ICC:
            case CvmType.ENCIPHERED_PIN_VERIFICATION_ICC_AND_SIGNATURE:
                if (doEncipheredPinVerification(msg.getPin())) {
                    if (cvmType == CvmType.PLAINTEXT_PIN_VERIFICATION_ICC_AND_SIGNATURE) {
                        getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.UNKNOWN);
                    } else {
                        getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.SUCCESSFUL);
                    }
                } else {
                    // failed
                    LogUtil.d(TAG, "Verify plain text offline pin failed. pinTryCounter=[" + pinTryCounter + "]");
                    checkAndPerformRetryOfflinePin(cvmRule);
                    return;
                }
                break;
            case CvmType.ENCIPHERED_PIN_VERIFIED_ONLINE:
                getEmvTransData().getTvr().markFlag(TVR.FLAG_ONLINE_PIN_ENTERED, true);
                getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.SUCCESSFUL);
                break;
        }

        processCvmVerificationResult(true);
    }

    private void checkAndPerformRetryOfflinePin(CvmRule cvmRule) {
        LogUtil.d(TAG, "Verify plain text offline pin failed. pinTryCounter=[" + pinTryCounter + "]");
        if (pinTryCounter <= 0) {
            /**
             * If the value of the PIN Try Counter is zero, indicating no remaining PIN tries,
             * the terminal should not allow offline PIN entry. The terminal:
             * • shall set the ‘PIN Try Limit exceeded’ bit in the TVR to 1 (for details on TVR, see Annex C of Book 3),
             * • shall not display any specific message regarding PINs, and
             * • shall continue cardholder verification processing in accordance with the card’s CVM List.
             */
            getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.FAILED);
            getEmvTransData().getTvr().markFlag(TVR.FLAG_PIN_TRY_LIMIT_EXCEEDED, true);
            checkIfPerformNextCvmRule();
        } else {
            pinTryCounter--;
            getEmvHandler().onRequestOfflinePIN(pinTryCounter);
        }
    }

    private void doPinBypassProcess(CvmRule cvmRule) {
        /**
         * If a PIN is required for entry as indicated in the card’s CVM List, an attended
         * terminal with an operational PIN pad may have the capability to bypass PIN
         * entry before or after several unsuccessful PIN tries.1 If this occurs, the terminal:
         * • shall set the ‘PIN entry required, PIN pad present, but PIN was not entered’ bit in the TVR to 1,
         * • shall not set the ‘PIN Try Limit exceeded’ bit in the TVR to 1,
         * • shall consider this CVM unsuccessful, and
         * • shall continue cardholder verification processing in accordance with the card’s CVM List.
         * When PIN entry has been bypassed for one PIN-related CVM, it may be
         * considered bypassed for any subsequent PIN-related CVM during the current transaction.
         */
        getEmvTransData().getTvr().markFlag(TVR.FLAG_PIN_REQ_PINPAD_NOT_PRESENT_PIN_WAS_NOT_ENTERED, true);
        getEmvTransData().getTvr().markFlag(TVR.FLAG_PIN_TRY_LIMIT_EXCEEDED, false);
        getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.FAILED);
        checkIfPerformNextCvmRule();
    }

    private boolean doPlainTextPinVerification(byte[] pin) {
        byte[] pinBlock = new byte[8];
        Arrays.fill(pinBlock, (byte) 0xFF);
        /**
         * The plaintext offline PIN block shall be formatted as follows:
         * C N P P P P P/F P/F P/F P/F P/F P/F P/F P/F F F
         * where:
         * Name Value
         * C Control field 4 bit binary number with value of 0010 (Hex '2')
         * N PIN length 4 bit binary number with permissible values of 0100 to 1100 (Hex '4' to 'C')
         * P PIN digit 4 bit binary number with permissible values of 0000 to 1001 (Hex '0' to '9')
         * P/F PIN/filler Determined by PIN length
         * F Filler 4 bit binary number with a value of 1111 (Hex 'F')
         */
        pinBlock[0] = (byte) (0x20 | pin.length);
        for (int i = 0; i < pin.length / 2; i++) {
            pinBlock[1+i] = (byte) ((pin[i*2] << 4 & 0xF0) | (pin[i*2+1] & 0x0F));
        }

        if (pin.length % 2 != 0) {
            pinBlock[(pin.length + 1) / 2] = (byte) (pin[pin.length - 1] << 4 | 0x0F);
        }

        LogUtil.d(TAG, "pinBlock hex=[" + StringUtil.byte2HexStr(pinBlock) + "]");

        // The data field of the command message contains the value field of tag '99'.
        getEmvTransData().getTagMap().put(TerminalTag.tag99, StringUtil.byte2HexStr(pinBlock));
        byte[] result = executeApduCmd(new VerifyCmd(false, pinBlock));
        VerifyResponse response = new VerifyResponse(result);

        return response.isSuccess();
    }

    private boolean doEncipheredPinVerification(byte[] pin) {
        return true;
    }

    private void doCvmVerification() {
        LogUtil.d(TAG, "doCvmVerification");
        TerminalCapabilities terminalCap = getEmvTransData().getTerminalCap();

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
        if (currentCvmRuleIndex == 0) {
            //When no CVM Conditions in CVM List are satisfied  Book 4 page 49
            getEmvTransData().getCvmResult().setCvmResult((byte)0x3F, (byte)0x00, CvmResult.FAILED);
        }
        processCvmVerificationResult(false);
    }

    private int getPINTryCounter() {
        if (pinTryCounter == -1) {
            byte[] result = executeApduCmd(new GetDataCmd(GetDataCmd.TYPE_PIN_TRY_COUNTER));
            GetDataResponse response = new GetDataResponse(result);
            if (response.isSuccess()) {
                response.saveTags(getEmvTransData().getTagMap());
                if (response.getTlvMap().containsKey(EMVTag.tag9F17)) {
                    String pinTryCounterHex = response.getTlvMap().get(EMVTag.tag9F17);
                    pinTryCounter = StringUtil.parseInt(pinTryCounterHex, 16, 0);
                    LogUtil.d(TAG, "Get data pinTryCounter=[" + pinTryCounter + "]");
                } else {
                    //If the PIN Try Counter is not retrievable or the GET DATA command is not
                    //supported by the ICC, or if the value of the PIN Try Counter is not zero,
                    //indicating remaining PIN tries, the terminal shall prompt for PIN entry such as
                    //by displaying the message ‘ENTER PIN’.
                    pinTryCounter = 100;
                }
            } else {
                LogUtil.d(TAG, "Get Data failed.");
                // Same with PIN Try Counter is not retrievable;
                pinTryCounter = 100;
            }
        } else {
            pinTryCounter--;
        }

        LogUtil.d(TAG, "pinTryCounter=[" + pinTryCounter + "]");
        return pinTryCounter;
    }

    private void checkIfPerformNextCvmRule() {
        CvmRule currentCvmRule = cvmList.getCvmRules().get(currentCvmRuleIndex);
        boolean isAllowFailedTryNext = (currentCvmRule.getCvmCode() & 0x40) > 0;
        LogUtil.d(TAG, "currentCvmRuleIndex=[" + currentCvmRuleIndex + " CvmCode=[" + currentCvmRule.getCvmCode() + "] isAllowFailedTryNext=[" + isAllowFailedTryNext + "]");
        if (isAllowFailedTryNext) {
            currentCvmRuleIndex++;
            doCvmVerification();
        } else {

        }
    }

    private void requestPerformCVM(CvmRule cvmRule) {
        if ((cvmRule.getCvmCode() & 0x80) > 0) {
            // When the terminal does not recognise any of the CVM
            //Codes in CVM List (or the code is RFU) where the CVM
            //Condition was satisfied
            LogUtil.d(TAG, "RFU cvm Code");
            getEmvTransData().getCvmResult().setCvmResult((byte)0x3F, (byte)0x00, CvmResult.FAILED);
            getEmvTransData().getTvr().markFlag(TVR.FLAG_UNRECOGNISED_CVM, true);
            processCvmVerificationResult(false);
            return;
        }

        byte cvmType = (byte) (cvmRule.getCvmCode() & 0x3F);
        LogUtil.d(TAG, "perform CVM code=[" + String.format("0x%02X", cvmRule.getCvmCode()) + "]");
        switch (cvmType) {
            case CvmType.PLAINTEXT_PIN_VERIFICATION_ICC:
            case CvmType.PLAINTEXT_PIN_VERIFICATION_ICC_AND_SIGNATURE:
            case CvmType.ENCIPHERED_PIN_VERIFICATION_ICC:
            case CvmType.ENCIPHERED_PIN_VERIFICATION_ICC_AND_SIGNATURE:
                if (isPinBypassedBefore) {
                    LogUtil.d(TAG, "isPinBypassedBefore is true");
                    doPinBypassProcess(cvmRule);
                    return;
                }

                int pinTryCounter = getPINTryCounter();
                if (pinTryCounter == 0) {
                    // If the value of the PIN Try Counter is zero, indicating no remaining PIN tries,
                    //the terminal should not allow offline PIN entry. The terminal:
                    //• shall set the ‘PIN Try Limit exceeded’ bit in the TVR to 1 (for details on TVR,
                    //see Annex C of Book 3),
                    //• shall not display any specific message regarding PINs, and
                    //• shall continue cardholder verification processing in accordance with the card’s
                    //CVM List.
                    getEmvTransData().getTvr().markFlag(TVR.FLAG_PIN_TRY_LIMIT_EXCEEDED, true);
                    checkIfPerformNextCvmRule();
                } else {
                    getEmvHandler().onRequestOfflinePIN(getPINTryCounter());
                }
                break;
            case CvmType.ENCIPHERED_PIN_VERIFIED_ONLINE:
                if (isPinBypassedBefore) {
                    LogUtil.d(TAG, "isPinBypassedBefore is true");
                    doPinBypassProcess(cvmRule);
                    return;
                }

                getEmvHandler().onRequestOnlinePIN();
                break;
            case CvmType.SIGNATURE:
                getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.UNKNOWN);
                processCvmVerificationResult(true);
                break;
            case CvmType.NO_CVM_REQUIRED:
                //When the applicable CVM is ‘No CVM required’, if the terminal supports
                //‘No CVM required’ it shall set byte 3 of the CVM Results to ‘successful’.
                getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.SUCCESSFUL);
                processCvmVerificationResult(true);
                break;
            case CvmType.FAIL_CVM_PROCESSING:
                //When the applicable CVM is ‘Fail CVM processing’, the terminal shall set byte 3 of the
                //CVM Results to ‘failed’.
                getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.FAILED);
                processCvmVerificationResult(false);
                break;
            default:
                getEmvTransData().getCvmResult().setCvmResult((byte)0x3F, (byte)0x00, CvmResult.FAILED);
                getEmvTransData().getTvr().markFlag(TVR.FLAG_UNRECOGNISED_CVM, true);
                processCvmVerificationResult(false);
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
