package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.GetChallengeCmd;
import com.vfi.android.emvkernel.corelogical.apdu.GetChallengeResponse;
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
import com.vfi.android.emvkernel.data.consts.EMVResultCode;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.emvkernel.data.consts.TransType;
import com.vfi.android.emvkernel.utils.SecurityUtil;
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
            getEmvTransData().getTvr().markFlag(TVR.FLAG_ICC_DATA_MISSING, true); // book 3 page 105
            jumpToState(EmvStateType.STATE_TERMINAL_RISK_MANAGEMENT);
            sendMessage(new Msg_StartTerminalRiskManagement());
            return;
        }

        String iccCvmList = getEmvTransData().getTagMap().get(EMVTag.tag8E);
        if (iccCvmList.length() <= 16) {
            // Note: A CVM List with no Cardholder Verification Rules is considered to be the same as
            //a CVM List not being present
            LogUtil.d(TAG, "NO Card holder verification rules present");
            getEmvTransData().getCvmResult().setCvmResult((byte)0x3F, (byte)0x00, CvmResult.UNKNOWN);
            getEmvTransData().getTvr().markFlag(TVR.FLAG_ICC_DATA_MISSING, true); // book 3 page 105
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
                if (!retrievalICCPinPublicKey()) {
                    LogUtil.d(TAG, "Check ICC PIN Encipher public key failed");
                    //If the conditions under points 1 and 2 above are not satisfied or if as
                    //described in Section 6.1.2 for dynamic data authentication, the Issuer
                    //Public Key Certificate has been revoked, then PIN encipherment has
                    //failed and the Offline Enciphered PIN CVM has failed.
                    getEmvTransData().getCvmResult().setCvmResult(cvmRule.getCvmCode(), cvmRule.getCvmConditionCode(), CvmResult.FAILED);
                    checkIfPerformNextCvmRule();
                    return;
                }

                if (doEncipheredPinVerification(msg.getPin())) {
                    if (cvmType == CvmType.ENCIPHERED_PIN_VERIFICATION_ICC_AND_SIGNATURE) {
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

    private byte[] getPinBlock(byte[] pin) {
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

        return pinBlock;
    }

    private boolean doPlainTextPinVerification(byte[] pin) {
        LogUtil.d(TAG, "doPlainTextPinVerification");

        byte[] pinBlock = getPinBlock(pin);
        LogUtil.d(TAG, "pinBlock hex=[" + StringUtil.byte2HexStr(pinBlock) + "]");

        // The data field of the command message contains the value field of tag '99'.
        getEmvTransData().getTagMap().put(TerminalTag.tag99, StringUtil.byte2HexStr(pinBlock));
        byte[] result = executeApduCmd(new VerifyCmd(false, pinBlock));
        VerifyResponse response = new VerifyResponse(result);
        //• The PIN is blocked upon initial use of the VERIFY command or if recovery of
        //the enciphered PIN Block has failed (the ICC returns SW1 SW2 = '6983' or
        //'6984' in response to the VERIFY command). In this case, the terminal shall
        //set the ‘PIN Try Limit exceeded’ bit in the TVR to 1.
        //• The number of remaining PIN tries is reduced to zero (indicated by an
        //SW1 SW2 of '63C0' in the response to the VERIFY command). In this case,
        //the terminal shall set the ‘PIN Try Limit exceeded’ bit in the TVR to 1.
        if (response.isPinBlocked()) {
            pinTryCounter = 0;
            setErrorCode(EMVResultCode.ERR_PIN_BLOCKED);
        } else if (response.isZeroRetryRemain()) {
            pinTryCounter = 0;
            setErrorCode(EMVResultCode.ERR_PIN_TRY_LIMIT_EXCEEDED);
        }

        return response.isSuccess();
    }

    private boolean doEncipheredPinVerification(byte[] pin) {
        byte[] result = executeApduCmd(new GetChallengeCmd());
        GetChallengeResponse response = new GetChallengeResponse(result);
        //When the response to the
        //GET CHALLENGE command is anything other than an 8 byte data value
        //with SW1 SW2 = '9000', then the terminal shall consider that the Offline
        //Enciphered PIN CVM has failed.
        if (!response.isSuccess() || response.getUnpredictableNumber() == null || response.getUnpredictableNumber().length != 8) {
            LogUtil.d(TAG, "GetChallenge failed.");
            return false;
        }

        /**
         * Field Name              |       Length     |       Description                                                               |  Format
         * --------------------------------------------------------------------------------------------------------------------------------------
         * Data Header             |       1          |            Hex Value '7F'                                                       |   b
         * PIN Block               |       8          |            PIN in PIN Block                                                     |   b
         * ICC Unpredictable Number|       8          |       Unpredictable number obtained from the ICC with the GET CHALLENGE command |   b
         * Random Padding          |    NIC – 17      |       Random Padding generated by the terminal                                  |   b
         * --------------------------------------------------------------------------------------------------------------------------------------
         *                              Table 25: Data to be Enciphered for PIN Encipherment
         */
        int iccPinPublicKeyLen = getEmvTransData().getIccPinPublicKey().length() / 2;
        LogUtil.d(TAG, "iccPinPublicKeyLen=[" + iccPinPublicKeyLen + "]");
        String pinDataHex = "7F" + StringUtil.byte2HexStr(getPinBlock(pin)) + StringUtil.byte2HexStr(response.getUnpredictableNumber()) + SecurityUtil.getRandomBytesAndBreakDown(iccPinPublicKeyLen - 17);
        LogUtil.d(TAG, "pinDataHex=[" + pinDataHex + "]");

        byte[] encipheredPINData = SecurityUtil.rsaRecovery(StringUtil.hexStr2Bytes(getEmvTransData().getIccPinPublicKey()), StringUtil.hexStr2Bytes(pinDataHex));
        LogUtil.d(TAG, "encipheredPINDataHex=[" + StringUtil.byte2HexStr(encipheredPINData) + "]");

        result = executeApduCmd(new VerifyCmd(true, encipheredPINData));
        VerifyResponse verifyResponse = new VerifyResponse(result);

        //• The PIN is blocked upon initial use of the VERIFY command or if recovery of
        //the enciphered PIN Block has failed (the ICC returns SW1 SW2 = '6983' or
        //'6984' in response to the VERIFY command). In this case, the terminal shall
        //set the ‘PIN Try Limit exceeded’ bit in the TVR to 1.
        //• The number of remaining PIN tries is reduced to zero (indicated by an
        //SW1 SW2 of '63C0' in the response to the VERIFY command). In this case,
        //the terminal shall set the ‘PIN Try Limit exceeded’ bit in the TVR to 1.
        if (verifyResponse.isPinBlocked()) {
            pinTryCounter = 0;
            setErrorCode(EMVResultCode.ERR_PIN_BLOCKED);
        } else if (verifyResponse.isZeroRetryRemain()) {
            pinTryCounter = 0;
            setErrorCode(EMVResultCode.ERR_PIN_TRY_LIMIT_EXCEEDED);
        }

        return verifyResponse.isSuccess();
    }

    private boolean retrievalICCPinPublicKey() {
        LogUtil.d(TAG, "retrievalICCPinPublicKey");

        if (!getEmvTransData().getTagMap().containsKey(EMVTag.tag9F2D)
                || !getEmvTransData().getTagMap().containsKey(EMVTag.tag9F2E)) {
            if (getEmvTransData().getIccPublicKey() != null) {
                //2. The ICC does not own a specific ICC PIN encipherment public key pair,
                //but owns an ICC public key pair for offline dynamic data authentication
                //as specified in section 6.1. This key pair can then be used for PIN
                //encipherment. The ICC Public Key is stored on the ICC in a public key
                //certificate as specified in section 6.1.
                LogUtil.d(TAG, "ICC PIN public key not found, use ICC public key as pin public key.");
                return true;
            } else {
                LogUtil.d(TAG, "Both ICC PIN public key and ICC public key not found.");
                return false;
            }
        }


        String iccPinPublicKeyCert = getEmvTransData().getTagMap().get(EMVTag.tag9F2D);
        String iccPinPublicKeyExponent = getEmvTransData().getTagMap().get(EMVTag.tag9F2E);
        String iccPinPublicKeyRemainder = getEmvTransData().getTagMap().get(EMVTag.tag9F2F);
        LogUtil.d(TAG, "iccPinPublicKeyCert=[" + iccPinPublicKeyCert + "]");
        LogUtil.d(TAG, "iccPinPublicKeyExponent=[" + iccPinPublicKeyExponent + "]");
        LogUtil.d(TAG, "iccPinPublicKeyRemainder=[" + iccPinPublicKeyRemainder + "]");


        // 1. If the ICC Public Key Certificate has a length different from the length of
        //the Issuer Public Key Modulus obtained in the previous section, offline
        //dynamic data authentication has failed.
        String issuerPublicKey = getEmvTransData().getIssuerPublicKey();
        if (issuerPublicKey.length() != iccPinPublicKeyCert.length()) {
            LogUtil.d(TAG, "ICC PIN Public Key Certificate has a length different from issuer public key");
            setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_LEN_DIFFERENT_FROM_ICC_PIN_PUBLIC_KEY_CERT);
            return false;
        }

        byte[] iccPinPublicKeyCertBytes = StringUtil.hexStr2Bytes(iccPinPublicKeyCert);
        byte[] issuerPublicKeyExponentBytes = StringUtil.hexStr2Bytes(getEmvTransData().getTagMap().get(EMVTag.tag9F32));
        byte[] iccPinPublicKeyCertRecoveredBytes = SecurityUtil.signVerify(iccPinPublicKeyCertBytes, issuerPublicKeyExponentBytes, StringUtil.hexStr2Bytes(issuerPublicKey));
        String iccPinPublicKeyCertRecoveredHex = StringUtil.byte2HexStr(iccPinPublicKeyCertRecoveredBytes);
        LogUtil.d(TAG, "iccPinPublicKeyCertRecoveredHex=[" + iccPinPublicKeyCertRecoveredHex + "]");

        // 2. In order to obtain the recovered data specified in Table 23, apply the
        //recovery function as specified in Annex A2.1 on the ICC Public Key
        //Certificate using the Issuer Public Key in conjunction with the
        //corresponding algorithm. If the Recovered Data Trailer is not equal to
        //'BC'

        if (iccPinPublicKeyCertRecoveredHex == null || iccPinPublicKeyCertRecoveredHex.length() < 42 * 2) {
            LogUtil.d(TAG, "Recovered data failed.");
            setErrorCode(EMVResultCode.ERR_ICC_PIN_PUB_KEY_RECOVERED_FAILED);
            return false;
        }

        if (!iccPinPublicKeyCertRecoveredHex.endsWith("BC")) {
            LogUtil.d(TAG, "Recovered Data Trailer is not equal to 'BC'");
            setErrorCode(EMVResultCode.ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_TRAILER_NOT_BC);
            return false;
        }

        //3. Check the Recovered Data Header. If it is not '6A', offline dynamic data
        //authentication has failed.
        if (!iccPinPublicKeyCertRecoveredHex.startsWith("6A")) {
            LogUtil.d(TAG, "Recovered Data Header is not equal to '6A'");
            setErrorCode(EMVResultCode.ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_HEADER_NOT_6A);
            return false;
        }

        //4. Check the Certificate Format. If it is not '02', offline dynamic data
        //authentication has failed.
        if (!iccPinPublicKeyCertRecoveredHex.substring(2, 4).equals("04")) {
            LogUtil.d(TAG, "Recovered Data Certificate Format not equal to '04'");
            setErrorCode(EMVResultCode.ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_CERTIFICATE_FORMAT_NOT_04);
            return false;
        }

        //5. Concatenate from left to right the second to the tenth data elements in
        //Table 14 (that is, Certificate Format through ICC Public Key or Leftmost
        //Digits of the ICC Public Key), followed by the ICC Public Key Remainder
        //(if present), the ICC Public Key Exponent, and finally the static data to be
        //authenticated specified in section 10.3 of Book 3. If the Static Data
        //Authentication Tag List is present and contains tags other than '82', then
        //offline dynamic data authentication has failed.
        //6. Apply the indicated hash algorithm (derived from the Hash Algorithm
        //Indicator) to the result of the concatenation of the previous step to
        //produce the hash result.
        //7. Compare the calculated hash result from the previous step with the
        //recovered Hash Result. If they are not the same, offline dynamic data
        //authentication has failed.
        String hexData = iccPinPublicKeyCertRecoveredHex.substring(2, iccPinPublicKeyCertRecoveredHex.length() - 2 - 40);
        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag9F2F)) {
            hexData += getEmvTransData().getTagMap().get(EMVTag.tag9F2F);
        }
        hexData += getEmvTransData().getTagMap().get(EMVTag.tag9F2E);

        if (getEmvTransData().isExistStaticDataRecordNotCodeWithTag70()) {
            LogUtil.d(TAG, "ICC PIN Public key, Read offline static data record not code with tag70");
            setErrorCode(EMVResultCode.ERR_READ_OFFLINE_STATIC_DATA_RECORD_NOT_CODED_WITH_TAG70);
            return false;
        }

        hexData += getEmvTransData().getStaticDataToBeAuthenticated();

        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag9F4A)) {
            String tag9F4AValue = getEmvTransData().getTagMap().get(EMVTag.tag9F4A);
            LogUtil.d(TAG, "Optional Static Data Authentication Tag List=[" + tag9F4AValue + "]");
            if (tag9F4AValue.length() > 0 && !tag9F4AValue.equals(EMVTag.tag82)) {
                LogUtil.d(TAG, "Optional Static Data Authentication Tag List not only tag82");
                setErrorCode(EMVResultCode.ERR_OPTIONAL_STATIC_DATA_AUTHENTICATION_TAG_LIST_NOT_ONLY_TAG82);
                return false;
            }
            hexData += getEmvTransData().getTagMap().get(EMVTag.tag82);
        }
        LogUtil.d(TAG, "hexData=[" + hexData + "]");

        int hashIndicatorStartIndex = (1+1+10+2+3) * 2;
        String hashIndicator = iccPinPublicKeyCertRecoveredHex.substring(hashIndicatorStartIndex, hashIndicatorStartIndex + 2);
        LogUtil.d(TAG, "hashIndicator=[" + hashIndicator + "]");
        if (hashIndicator.equals("01")) {
            String hashHex = SecurityUtil.calculateSha1(hexData);
            String iccKeyCertificateHash = iccPinPublicKeyCertRecoveredHex.substring(iccPinPublicKeyCertRecoveredHex.length() - 2 - 40, iccPinPublicKeyCertRecoveredHex.length() - 2);
            LogUtil.d(TAG, "calculate hash hex=[" + hashHex + "], certificate hash hex=[" + iccKeyCertificateHash + "]");

            if (!iccKeyCertificateHash.equals(hashHex)) {
                LogUtil.d(TAG, "Hash not the same, recover icc pin public key failed.");
                setErrorCode(EMVResultCode.ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_CERTIFICATE_HASH_WRONG);
                return false;
            }
        } else {
            LogUtil.d(TAG, "Hash indicator =[" + hashIndicator + "] not support");
            setErrorCode(EMVResultCode.ERR_HASH_INDICATOR_ALGO_NOT_SUPPORT);
            return false;
        }

        //8. Compare the recovered PAN to the Application PAN read from the ICC. If
        //they are not the same, offline dynamic data authentication has failed.
        int applicationPanStartIndex = (1+1) * 2;
        String applicationPanHex = iccPinPublicKeyCertRecoveredHex.substring(applicationPanStartIndex, applicationPanStartIndex + 10 * 2);
        applicationPanHex = applicationPanHex.replace("F", "");
        String panHex = getEmvTransData().getTagMap().get(EMVTag.tag5A);
        if (!panHex.startsWith(applicationPanHex)) {
            LogUtil.d(TAG, "Verify Application Pan failed.");
            setErrorCode(EMVResultCode.ERR_VERIFY_APPLICATION_PAN_FAILED);
            return false;
        }

        //9. Verify that the last day of the month specified in the Certificate
        //Expiration Date is equal to or later than today’s date. If not, offline
        //dynamic data authentication has failed.
        int certificateExpirationDateStartIndex = (1+1+10) * 2;
        // format MMYY
        String certificateExpirationDate = iccPinPublicKeyCertRecoveredHex.substring(certificateExpirationDateStartIndex, certificateExpirationDateStartIndex + 2 * 2);
        String year = certificateExpirationDate.substring(2);
        String MM = certificateExpirationDate.substring(0, 2);
        if (StringUtil.parseInt(year, 0) <= 50) {
            year = "20" + year;
        } else {
            year = "19" + year;
        }
        certificateExpirationDate = year + MM + StringUtil.getLastDayOfThisMonth();
        String currentYYYYMMDD = StringUtil.getSystemDate();
        LogUtil.d(TAG, "currentYYYYMMDD=[" + currentYYYYMMDD + "] iccPinPublicKeyExpirationDate=[" + certificateExpirationDate + "]");
        if (StringUtil.parseInt(currentYYYYMMDD, 0) >= StringUtil.parseInt(certificateExpirationDate, 0)) {
            LogUtil.d(TAG, "Public key certificate expiration");
            setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_EXPIRATION);
            return false;
        }

        //10. If the ICC Public Key Algorithm Indicator is not recognised, offline
        //dynamic data authentication has failed.
        int publicKeyAlgIndicatorStartIndex = (1+1+10+2+3+1) * 2;
        String publicKeyAlgIndicator = iccPinPublicKeyCertRecoveredHex.substring(publicKeyAlgIndicatorStartIndex, publicKeyAlgIndicatorStartIndex+2);
        if (!publicKeyAlgIndicator.equals("01")) {
            LogUtil.d(TAG, "Public key Algorithm Indicator is not recognised");
            setErrorCode(EMVResultCode.ERR_PUBLIC_KEY_ALG_INDICATOR_IS_NOT_RECOGNISED);
            return false;
        }

        int iccPinPublicKeyLenStartIndex = (1+1+10+2+3+1+1) * 2;
        String iccPinPublicKeyLenHex = iccPinPublicKeyCertRecoveredHex.substring(iccPinPublicKeyLenStartIndex, iccPinPublicKeyLenStartIndex+2);
        int iccPinPublicKeyLen = StringUtil.parseInt(iccPinPublicKeyLenHex, 16,0);
        // hex len need double
        iccPinPublicKeyLen = iccPinPublicKeyLen * 2;
        LogUtil.d(TAG, "iccPinPublicKeyLen=[" + iccPinPublicKeyLen + "]");

        int iccPinPublicKeyRemainderLen = iccPinPublicKeyRemainder == null ? 0 : iccPinPublicKeyRemainder.length();
        LogUtil.d(TAG, "iccPinPublicKeyRemainderLen=[" + iccPinPublicKeyRemainderLen + "]");

        int iccPinPublicKeyStartIndex = (1+1+10+2+3+1+1+1+1) * 2;
        int iccPinPublicKeyLeftMostDigitLen = iccPinPublicKeyLen - iccPinPublicKeyRemainderLen;
        String iccPinPublicKey = iccPinPublicKeyCertRecoveredHex.substring(iccPinPublicKeyStartIndex, iccPinPublicKeyStartIndex + iccPinPublicKeyLeftMostDigitLen);
        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag9F2F)) {
            iccPinPublicKey += getEmvTransData().getTagMap().get(EMVTag.tag9F2F);
        }
        LogUtil.d(TAG, "iccPinPublicKey=[" + iccPinPublicKey + "]");
        getEmvTransData().setIccPinPublicKey(iccPinPublicKey);

        LogUtil.d(TAG, "retrievalICCPinPublicKey success");

        return true;
    }

    private void doCvmVerification() {
        LogUtil.d(TAG, "doCvmVerification");
        TerminalCapabilities terminalCap = getEmvTransData().getTerminalCap();

        if (cvmList == null) {
            String iccCvmList = getEmvTransData().getTagMap().get(EMVTag.tag8E);
            cvmList = new CvmList(iccCvmList);
            // no cvm rules is checked before, no need check it again.
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
            LogUtil.d(TAG, "current CVM rule not support try next rule.");
            processCvmVerificationResult(false);
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
                if (isPinBypassedBefore && getEmvContext().getEmvParams().isBypassAll()) {
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
                if (isPinBypassedBefore && getEmvContext().getEmvParams().isBypassAll()) {
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

                // If the CVM is not recognised, the terminal shall set the ‘Unrecognised CVM’
                //bit in the TVR (b7 of byte 3) to 1 and processing continues at step 2.
                getEmvTransData().getTvr().markFlag(TVR.FLAG_UNRECOGNISED_CVM, true);
                return false;
        }

        // application currency = That is, Transaction Currency Code = Application Currency Code.

        byte cvmType = (byte) (cvmRule.getCvmCode() & 0x3F);
        boolean isPinRelatedCvm = false;
        switch (cvmType) {
            case CvmType.PLAINTEXT_PIN_VERIFICATION_ICC:
                isPinRelatedCvm = true;
                if (terminalCap.isSupportPlainTextPinVerifyByICC()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] PLAINTEXT_PIN_VERIFICATION_ICC");
                    return true;
                }
                break;
            case CvmType.ENCIPHERED_PIN_VERIFIED_ONLINE:
                isPinRelatedCvm = true;
                if (terminalCap.isSupportEncipheredPINForOnlineVerification()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] ENCIPHERED_PIN_VERIFIED_ONLINE");
                    return true;
                }
                break;
            case CvmType.PLAINTEXT_PIN_VERIFICATION_ICC_AND_SIGNATURE:
                isPinRelatedCvm = true;
                //Some CVMs require multiple verification methods (for example, offline PIN plus
                //signature). For these CVMs, all methods in the CVM must be successful for
                //cardholder verification to be considered successful.
                if (terminalCap.isSupportPlainTextPinVerifyByICC() && terminalCap.isSupportSignaturePaper()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] PLAINTEXT_PIN_VERIFICATION_ICC_AND_SIGNATURE");
                    return true;
                }
                break;
            case CvmType.ENCIPHERED_PIN_VERIFICATION_ICC:
                isPinRelatedCvm = true;
                if (terminalCap.isSupportEncipheredPINForOfflineVerification()) {
                    LogUtil.d(TAG, "Match currentCvmRuleIndex=[" + currentCvmRuleIndex + "] ENCIPHERED_PIN_VERIFICATION_ICC");
                    return true;
                }
                break;
            case CvmType.ENCIPHERED_PIN_VERIFICATION_ICC_AND_SIGNATURE:
                isPinRelatedCvm = true;
                //Some CVMs require multiple verification methods (for example, offline PIN plus
                //signature). For these CVMs, all methods in the CVM must be successful for
                //cardholder verification to be considered successful.
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

        if (isPinRelatedCvm) {
            LogUtil.d(TAG, "terminal not support pin related cvm method");
            getEmvTransData().getTvr().markFlag(TVR.FLAG_PIN_REQ_PINPAD_NOT_PRESENT_OR_NOT_WORKING, true);
        }

        return false;
    }
}
