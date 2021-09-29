package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.GenerateApplicationCryptogramCmd;
import com.vfi.android.emvkernel.corelogical.apdu.GenerateApplicationCryptogramResponse;
import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartTerminalActionAnalysis;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.beans.DOLBean;
import com.vfi.android.emvkernel.data.beans.EmvResultInfo;
import com.vfi.android.emvkernel.data.beans.tagbeans.TSI;
import com.vfi.android.emvkernel.data.beans.tagbeans.TVR;
import com.vfi.android.emvkernel.data.consts.EMVResultCode;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.ParamTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.emvkernel.utils.DOLUtil;
import com.vfi.android.emvkernel.utils.SecurityUtil;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.List;
import java.util.Map;

public class TerminalActionAnalysisState extends AbstractEmvState {
    public TerminalActionAnalysisState() {
        super(EmvStateType.STATE_TERMINAL_ACTION_ANALYSIS);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        Message message = context.getMessage();
        LogUtil.d(TAG, "TerminalActionAnalysisState msgType=" + message.getMessageType());
        if (message instanceof Msg_StartTerminalActionAnalysis) {
            processStartTerminalActionAnalysisMessage(message);
        }
    }

    private void processStartTerminalActionAnalysisMessage(Message message) {
        LogUtil.d(TAG, "TVR=[" + getEmvTransData().getTvr().getTVRHex() + "]");
        String tacDefault = getTerminalActionCode(ParamTag.TAC_DEFAULT);
        String tacOnline = getTerminalActionCode(ParamTag.TAC_ONLINE);
        String tacDenial = getTerminalActionCode(ParamTag.TAC_DENIAL);

        String iacDefault = getIssuerActionCode(EMVTag.tag9F0D);
        String iacOnline = getIssuerActionCode(EMVTag.tag9F0F);
        String iacDenial = getIssuerActionCode(EMVTag.tag9F0E);
        LogUtil.d(TAG, "iacDefault=[" + iacDefault + "]");
        LogUtil.d(TAG, "tacDefault=[" + tacDefault + "]");

        LogUtil.d(TAG, "iacOnline=[" + iacOnline + "]");
        LogUtil.d(TAG, "tacOnline=[" + tacOnline + "]");

        LogUtil.d(TAG, "iacDenial=[" + iacDenial + "]");
        LogUtil.d(TAG, "tacDenial=[" + tacDenial + "]");

        TVR tacDefaultVal = new TVR(tacDefault);
        TVR tacOnlineVal = new TVR(tacOnline);
        TVR tacDenialVal = new TVR(tacDenial);
        TVR iacDefaultVal = new TVR(iacDefault);
        TVR iacOnlineVal = new TVR(iacOnline);
        TVR iacDenialVal = new TVR(iacDenial);

        boolean isRequireCDA = getEmvTransData().isDoCDAInFirstGAC();
        LogUtil.d(TAG, "First GAC isRequireCDA=[" + isRequireCDA + "]");
        /**
         * 1. Together, the Issuer Action Code - Denial and the Terminal
         * Action Code - Denial specify the conditions that cause denial of a transaction
         * without attempting to go online. If either data object exists, the terminal shall
         * inspect each bit in the TVR. For each bit in the TVR that has a value of 1, the
         * terminal shall check the corresponding bits in the Issuer Action Code - Denial
         * and the Terminal Action Code - Denial. If the corresponding bit in either of the
         * action codes is set to 1, it indicates that the issuer or the acquirer wishes the
         * transaction to be rejected offline. In this case, the terminal shall issue a
         * GENERATE AC command to request an AAC from the ICC. This AAC may be
         * presented to the issuer to prove card presence during this transaction, but details
         * of handling a rejected transaction are outside the scope of this specification.
         */
        if (isExistTvrFlagTrue(tacDenialVal, iacDenialVal)) {
            performAAC(true, isRequireCDA);
            makeCardRiskManagementWasPerform();
            return;
        }


        boolean isOfflineOnlyTerminal = getEmvTransData().getTerminalType().isOfflineOnly();
        LogUtil.d(TAG, "isOfflineOnlyTerminal=[" + isOfflineOnlyTerminal + "]");
        if (isOfflineOnlyTerminal) { // offline terminal
            /**
             * 3. Together, the Issuer Action Code - Default and the
             * Terminal Action Code - Default specify the conditions that cause the transaction
             * to be rejected if it might have been approved online but the terminal is for any
             * reason unable to process the transaction online. The Issuer Action Code - Default
             * and the Terminal Action Code - Default are used only if the Issuer Action Code -
             * Online and the Terminal Action Code - Online were not used (for example, in
             * case of an offline-only terminal) or indicated a desire on the part of the issuer or
             * the acquirer to process the transaction online but the terminal was unable to go
             * online. In the event that an online-only terminal was unable to successfully go
             * online, it may optionally skip TAC/IAC Default processing (shown in Figure 7 for
             * a transaction that was not completed on-line)16. If an online-only terminal does
             * skip TAC/IAC Default processing, it shall request an AAC with the second
             * GENERATE AC command. If the terminal has not already rejected the
             * transaction and the terminal is for any reason unable to process the transaction
             * online, the terminal shall use this code to determine whether to approve or reject
             * the transaction offline. If any bit in Issuer Action Code - Default or the Terminal
             * Action Code - Default and the corresponding bit in the TVR are both set to 1, the
             * transaction shall be rejected and the terminal shall request an AAC to complete
             * processing. If no such condition appears, the transaction may be approved offline,
             * and a GENERATE AC command shall be issued to the ICC requesting a TC.
             * If CDA is to be performed (as described in section 10.3 of this book and section
             * 6.6 of Book 2), the terminal shall set the bit for ‘CDA Signature Requested’ in the
             * GENERATE AC command to 1.
             */
            if (isExistTvrFlagTrue(tacDefaultVal, iacDefaultVal)) {
                performAAC(true, isRequireCDA);
            } else {
                performTC(true, isRequireCDA);
            }
        } else { // online terminal
            /**
             * 2. Together, the Issuer Action Code - Online and the
             * Terminal Action Code - Online specify the conditions that cause a transaction to
             * be completed online. These data objects are meaningful only for terminals
             * capable of online processing. Offline-only terminals may skip this test and
             * proceed to checking the Issuer Action Code - Default and Terminal Action Code -
             * Default, described below. For an online-only terminal, if it has not already
             * decided to reject the transaction as described above, it shall continue transaction
             * processing online, and shall issue a GENERATE AC command requesting an
             * ARQC from the card. For a terminal capable of online processing, if the terminal
             * has not already decided to reject the transaction as described above, the terminal
             * shall inspect each bit in the TVR. For each bit in the TVR that has a value of 1,
             * the terminal shall check the corresponding bits in both the Issuer Action Code -
             * Online and the Terminal Action Code - Online. If the bit in either of the action
             * codes is set to 1, the terminal shall complete transaction processing online and
             * shall issue a GENERATE AC command requesting an ARQC from the ICC.
             * Otherwise, the terminal shall issue a GENERATE AC command requesting a TC
             * from the ICC.
             */
            if (isExistTvrFlagTrue(tacOnlineVal, iacOnlineVal)) {
                performARQC(true, isRequireCDA);
            } else {
                performTC(true, isRequireCDA);
            }
        }

        makeCardRiskManagementWasPerform();
    }

    private void performAAC(boolean isFirstGAC, boolean isRequireCDA) {
        LogUtil.d(TAG, "TAA result AAC.");
        // rejected transaction offline

        if (isRequireCDA) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED, true);
        }

        /**
         * CDA no need perform when execute AAC.
         *
         * Book2: Page70
         * The cryptogram to be requested is not an Application Authentication
         * Cryptogram (AAC), i.e. Terminal Action Analysis has not resulted in
         * offline decline.
         *
         * Book2: Page71
         * When requesting an AAC, the terminal shall request it without a CDA
         * signature.
         */
        byte[] ret = executeApduCmd(new GenerateApplicationCryptogramCmd(GenerateApplicationCryptogramCmd.TYPE_AAC, false, getGenerateACCommandData(true)));
        GenerateApplicationCryptogramResponse response = new GenerateApplicationCryptogramResponse(false, ret);
        response.saveTags(getEmvTransData().getTagMap());

        setErrorCode(EMVResultCode.ERR_TAA_RESULT_AAC);
        getEmvHandler().onTransactionResult(new EmvResultInfo(true, getEmvTransData().getErrorCode()));
        finishEmv();
    }

    private void performARQC(boolean isFirstGAC, boolean isRequireCDA) {
        LogUtil.d(TAG, "TAA result ARQC.");
        /**
         * Book2, Page70
         * In the case of the first GENERATE AC command:
         * • When requesting an ARQC, the terminal may request it with or without a
         * CDA signature. When an ARQC is requested without a CDA signature,
         * then the terminal shall set the TVR bit for 'Offline data authentication
         * EMV 4.3 Book 2 6 Offline Dynamic Data Authentication
         * Security and Key Management 6.6 Combined DDA/AC Generation (CDA) November 2011 Page 71
         * was not performed' to 124 prior to issuance of the GENERATE AC command.
         */
        if (isFirstGAC && !isRequireCDA) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED, true);
        }
        // transaction online
        byte[] ret = executeApduCmd(new GenerateApplicationCryptogramCmd(GenerateApplicationCryptogramCmd.TYPE_ARQC, isRequireCDA, getGenerateACCommandData(true)));
        GenerateApplicationCryptogramResponse response = new GenerateApplicationCryptogramResponse(isRequireCDA, ret);
        response.saveTags(getEmvTransData().getTagMap());
        if (response.isSuccess()) {
            if (response.isCardAAC()) {
                LogUtil.d(TAG, "Card reject transaction.");
                setErrorCode(EMVResultCode.ERR_TAA_RESULT_CARD_AAC);
                getEmvHandler().onTransactionResult(new EmvResultInfo(true, getEmvTransData().getErrorCode()));
                finishEmv();
                return;
            }

            if (isRequireCDA) {
                /**
                 * Book2, Page79 right bottom part
                 */
                if (!processCDAResult(isFirstGAC, response)) {
                    LogUtil.d(TAG, "Card request ARQC but CDA failed.");
                    performAAC(false, false);
                }
            }

            return;
        } else {

        }
    }

    private void performTC(boolean isFirstGAC, boolean isRequireCDA) {
        LogUtil.d(TAG, "TAA result TC.");
        if (isFirstGAC) {
            /**
             * Book2, Page71
             *
             * When requesting a TC, the terminal shall request it with a CDA signature.
             */
            boolean isRequireCDA2GAC = getEmvTransData().isDoCDAInSecondGAC();
            isRequireCDA = isRequireCDA2GAC || isRequireCDA;
            LogUtil.d(TAG, "PerformTC isRequireCDA=" + isRequireCDA);
        } else if (isRequireCDA) {
            /**
             * In the case of the second GENERATE AC command:
             * • The terminal shall set the TVR bit for 'Offline data authentication was not
             * performed' to 025 prior to issuance of the GENERATE AC command. If the
             * terminal is processing the transaction as ‘unable to go online’ then the
             * TVR bit setting shall be done before the associated terminal action analysis.
             *
             * • When requesting a TC:
             *  If the terminal is processing the transaction as 'unable to go online'
             * (and the result of terminal action analysis is to request a TC), then the
             * terminal shall request a TC with a CDA signature.
             *  If the terminal is not processing the transaction as ‘unable to go
             * online’, then the terminal may request the TC with or without a CDA signature.
             */
            getEmvTransData().getTvr().markFlag(TVR.FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED, false);
        }
        // transaction offline approval
        byte[] ret = executeApduCmd(new GenerateApplicationCryptogramCmd(GenerateApplicationCryptogramCmd.TYPE_TC, isRequireCDA, getGenerateACCommandData(true)));
        GenerateApplicationCryptogramResponse response = new GenerateApplicationCryptogramResponse(isRequireCDA, ret);
        response.saveTags(getEmvTransData().getTagMap());
        if (response.isSuccess()) {
            if (response.isCardAAC()) {
                LogUtil.d(TAG, "Card reject transaction.");
                doFinishProcess(false, EMVResultCode.ERR_TAA_RESULT_CARD_AAC);
                return;
            }

            if (isRequireCDA) {
                /**
                 * Book2, Page79 right bottom part
                 */
                if (!processCDAResult(isFirstGAC, response)) {
                    LogUtil.d(TAG, "Card request TC but CDA failed.");
                    doFinishProcess(false, EMVResultCode.ERR_TAA_CDA_FAILED);
                }
            }
        } else {
            LogUtil.d(TAG, "Do GenerateAC command failed, error status=[" + response.getStatus() + "]");
            doFinishProcess(false, EMVResultCode.ERR_TAA_EXECUTE_GAC_FAILED);
        }
    }

    private void doFinishProcess(boolean isApproval, int errorCode) {
        if (isApproval) {
            setErrorCode(EMVResultCode.SUCCESS);
        } else {
            setErrorCode(errorCode);
        }
        getEmvHandler().onTransactionResult(new EmvResultInfo(!isApproval, getEmvTransData().getErrorCode()));
        finishEmv();
    }

    private boolean processCDAResult(boolean isFirstGAC, GenerateApplicationCryptogramResponse response) {
        boolean isSuccess = false;

        if (response.getIssuerApplicationData() != null) {
            isSuccess = verifySignedDynamicApplicationData(isFirstGAC, response);
        }

        if (!isSuccess) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED, false);
            getEmvTransData().getTvr().markFlag(TVR.FLAG_CDA_FAILED, true);
            getEmvTransData().getTsi().markFlag(TSI.FLAG_OFFLINE_DATA_AUTH_WAS_PERFORMED, true);
        }

        LogUtil.d(TAG, "doCDAResultProcess isSuccess=[" + isSuccess + "]");
        return isSuccess;
    }

    private boolean verifySignedDynamicApplicationData(boolean isFirstGAC, GenerateApplicationCryptogramResponse response) {
        LogUtil.d(TAG, "verifySignedDynamicApplicationData");

        String signedDynamicApplicationData = response.getSignedDynamicApplicationData();
        /**
         * 1. If the Signed Dynamic Application Data has a length different from the
         * length of the ICC Public Key Modulus, CDA has failed.
         */
        LogUtil.d(TAG, "Signed Dynamic Application Data=[" + signedDynamicApplicationData + "]");
        String iccPublicKey = getEmvTransData().getIccPublicKey();

        if (iccPublicKey == null || iccPublicKey.length() != signedDynamicApplicationData.length()) {
            LogUtil.d(TAG, "Signed Dynamic Application Data has a length different from the length of the ICC Public Key Modulus");
            setErrorCode(EMVResultCode.ERR_SIGNED_DYNAMIC_APP_DATA_HAVE_DIFFERENT_LENGTH_WITH_PUBLIC_KEY);
            return false;
        }

        byte[] signedDynamicApplicationDataBytes = StringUtil.hexStr2Bytes(signedDynamicApplicationData);
        byte[] iccPublicKeyModulesBytes = StringUtil.hexStr2Bytes(iccPublicKey);
        byte[] iccPublicKeyExponent = StringUtil.hexStr2Bytes(getEmvTransData().getTagMap().get(EMVTag.tag9F47));
        byte[] iccSignDataRecoveredBytes = SecurityUtil.signRecover(signedDynamicApplicationDataBytes, iccPublicKeyExponent, iccPublicKeyModulesBytes);
        String recoveredSignedDynamicAppDataHex = StringUtil.byte2HexStr(iccSignDataRecoveredBytes);
        LogUtil.d(TAG, "recoveredSignedDynamicAppDataHex=[" + recoveredSignedDynamicAppDataHex + "]");

        if (recoveredSignedDynamicAppDataHex == null || recoveredSignedDynamicAppDataHex.length() < 25 * 2) {
            LogUtil.d(TAG, "Recovered sign dynamic app data failed.");
            setErrorCode(EMVResultCode.ERR_SIGNED_DYNAMIC_APP_DATA_RECOVERED_FAILED);
            return false;
        }

        /**
         * 2. To obtain the recovered data specified in Table 22, apply the recovery
         * function as specified in Annex A2.1 on the Signed Dynamic Application
         * Data using the ICC Public Key in conjunction with the corresponding
         * algorithm. If the Recovered Data Trailer is not equal to 'BC', CDA has failed.
         */
        if (!recoveredSignedDynamicAppDataHex.endsWith("BC")) {
            LogUtil.d(TAG, "Recovered Data Trailer is not equal to 'BC'");
            setErrorCode(EMVResultCode.ERR_SIGNED_DYNAMIC_APP_DATA_TRAILER_NOT_BC);
            return false;
        }

        // 3. Check the Recovered Data Header. If it is not '6A', CDA has failed.
        if (!recoveredSignedDynamicAppDataHex.startsWith("6A")) {
            LogUtil.d(TAG, "Recovered Data Header is not equal to '6A'");
            setErrorCode(EMVResultCode.ERR_SIGNED_DYNAMIC_APP_DATA_HEADER_NOT_6A);
            return false;
        }

        //4. Check the Signed Data Format. If it is not '05', CDA has failed.
        if (!recoveredSignedDynamicAppDataHex.startsWith("05", 2)) {
            LogUtil.d(TAG, "Recovered Data Certificate Format not equal to '05'");
            setErrorCode(EMVResultCode.ERR_SIGNED_DYNAMIC_APP_DATA_CERTIFICATE_FORMAT_NOT_05);
            return false;
        }

        // 5. Retrieve from the ICC Dynamic Data the data specified in Table 19.
        /**
         * Book2, Page73
         * Length           Value                     Format
         * ----------|------------------------------|---------------------
         * 1            ICC Dynamic Number Length     b
         * 2-8          ICC Dynamic Number            b
         * 1            Cryptogram Information Data   b
         * 8            TC or ARQC                    b
         * 20           Transaction Data Hash Code    b
         * ---------------------------------------------------------------
         * Table 19: 32-38 Leftmost Bytes of ICC Dynamic Data
         */
        int iccDynamicDataLengthStartIndex = (1+1+1) * 2;
        String iccDynamicDataLengthHex = recoveredSignedDynamicAppDataHex.substring(iccDynamicDataLengthStartIndex, iccDynamicDataLengthStartIndex + 2);
        int iccDynamicDataLength = StringUtil.parseInt(iccDynamicDataLengthHex, 16, 0);
        LogUtil.d(TAG, "iccDynamicDataLength=[" + iccDynamicDataLength + "]");

        int iccDynamicDataStartIndex = (1+1+1+1) * 2;
        String iccDynamicDataHex = recoveredSignedDynamicAppDataHex.substring(iccDynamicDataStartIndex, iccDynamicDataStartIndex + iccDynamicDataLength * 2);
        LogUtil.d(TAG, "iccDynamicDataHex=[" + iccDynamicDataHex + "]");

        if (iccDynamicDataHex.length() < 32 * 2 || iccDynamicDataHex.length() > 38 * 2) {
            LogUtil.d(TAG, "Wrong icc dynamic data");
            setErrorCode(EMVResultCode.ERR_WRONG_ICC_DYNAMIC_DATA);
            return false;
        }

        int index = 0;
        String iccDynamicNumberLengthHex = iccDynamicDataHex.substring(index, index + 2);
        int iccDynamicNumberLength = StringUtil.parseInt(iccDynamicNumberLengthHex, 16, 0);
        LogUtil.d(TAG, "iccDynamicNumberLength=[" + iccDynamicNumberLength + "]");
        index += 2;

        String iccDynamicNumberHex = iccDynamicDataHex.substring(index, index + iccDynamicNumberLength * 2);
        LogUtil.d(TAG, "iccDynamicNumberHex=[" + iccDynamicNumberHex + "]");
        getEmvTransData().getTagMap().put(EMVTag.tag9F4C, iccDynamicNumberHex);
        LogUtil.d(TAGS.SAVE_TAG, "putTag tag[" + EMVTag.tag9F4C + "]=[" + iccDynamicNumberHex + "]");
        index += iccDynamicNumberLength * 2;

        String cryptogramInformationDataHex = iccDynamicDataHex.substring(index, index + 2);
        LogUtil.d(TAG, "cryptogramInformationDataHex=[" + cryptogramInformationDataHex + "]");
        index += 2;

        String tcOrArqcHex = iccDynamicDataHex.substring(index, index + 8 * 2);
        LogUtil.d(TAG, "tcOrArqcHex=[" + tcOrArqcHex + "]");
        getEmvTransData().getTagMap().put(EMVTag.tag9F26, tcOrArqcHex);
        LogUtil.d(TAGS.SAVE_TAG, "putTag tag[" + EMVTag.tag9F26 + "]=[" + tcOrArqcHex + "]");
        index += 16;

        String transDataHashCode = iccDynamicDataHex.substring(index, index + 20 * 2);
        LogUtil.d(TAG, "transDataHashCode=[" + transDataHashCode + "]");

        /**
         * 6. Check that the Cryptogram Information Data retrieved from the ICC
         * Dynamic Data is equal to the Cryptogram Information Data obtained from
         * the response to the GENERATE AC command. If this is not the case, CDA
         * has failed.
         */
        if (!cryptogramInformationDataHex.equals(response.getCid())) {
            LogUtil.d(TAG, "CID data is different");
            setErrorCode(EMVResultCode.ERR_CID_DATA_DIFFERENT);
            return false;
        }

        /**
         * 7. Concatenate from left to right the second to the sixth data elements in
         * Table 22 (that is, Signed Data Format through Pad Pattern), followed by
         * the Unpredictable Number.
         * 8. Apply the indicated hash algorithm (derived from the Hash Algorithm
         * Indicator) to the result of the concatenation of the previous step to
         * produce the hash result.
         * 9. Compare the calculated hash result from the previous step with the
         * recovered Hash Result. If they are not the same, CDA has failed.
         */
        String hexData = recoveredSignedDynamicAppDataHex.substring(2, recoveredSignedDynamicAppDataHex.length() - 2 - 40);
        String unpredictableNumber = getEmvTransData().getTagMap().get(EMVTag.tag9F37);
        hexData += unpredictableNumber;
        LogUtil.d(TAG, "hexData=[" + hexData + "]");

        int hashIndicatorStartIndex = (1+1) * 2;
        String hashIndicator = recoveredSignedDynamicAppDataHex.substring(hashIndicatorStartIndex, hashIndicatorStartIndex + 2);
        LogUtil.d(TAG, "hashIndicator=[" + hashIndicator + "]");
        if (hashIndicator.equals("01")) {
            String hashHex = SecurityUtil.calculateSha1(hexData);
            String signedDynamicApplicationDataHash = recoveredSignedDynamicAppDataHex.substring(recoveredSignedDynamicAppDataHex.length() - 2 - 40, recoveredSignedDynamicAppDataHex.length() - 2);
            LogUtil.d(TAG, "calculate hash hex=[" + hashHex + "], signedDynamicApplicationDataHash hash hex=[" + signedDynamicApplicationDataHash + "]");

            if (!signedDynamicApplicationDataHash.equals(hashHex)) {
                LogUtil.d(TAG, "Hash not the same, CDA failed.");
                setErrorCode(EMVResultCode.ERR_SIGNED_DYNAMIC_APP_DATA_RECOVERED_DATA_HASH_WRONG);
                return false;
            }
        } else {
            LogUtil.d(TAG, "Hash indicator =[" + hashIndicator + "] not support");
            setErrorCode(EMVResultCode.ERR_SIGNED_DYNAMIC_APP_DATA_HASH_INDICATOR_ALGO_NOT_SUPPORT);
            return false;
        }

        /**
         * 10. Concatenate from left to right the values of the following data elements:
         * In the case of the first GENERATE AC command:
         *  The values of the data elements specified by, and in the order they
         * appear in the PDOL, and sent by the terminal in the GET
         * PROCESSING OPTIONS command.
         *  The values of the data elements specified by, and in the order they
         * appear in the CDOL1, and sent by the terminal in the first
         * GENERATE AC command.
         *  The tags, lengths, and values of the data elements returned by the ICC
         * in the response to the GENERATE AC command in the order they are
         * returned, with the exception of the Signed Dynamic Application Data.
         * In the case of the second GENERATE AC command:
         *  The values of the data elements specified by, and in the order they
         * appear in the PDOL, and sent by the terminal in the GET
         * PROCESSING OPTIONS command.
         *  The values of the data elements specified by, and in the order they
         * appear in the CDOL1, and sent by the terminal in the first
         * GENERATE AC command.
         *  The values of the data elements specified by, and in the order they
         * appear in the CDOL2, and sent by the terminal in the second
         * GENERATE AC command.
         *  The tags, lengths, and values of the data elements returned by the ICC
         * in the response to the GENERATE AC command in the order they are
         * returned, with the exception of the Signed Dynamic Application Data.
         *
         * 11. Apply the indicated hash algorithm (derived from the Hash Algorithm
         * Indicator) to the result of the concatenation of the previous step to
         * produce the Transaction Data Hash Code.
         *
         * 12. Compare the calculated Transaction Data Hash Code from the previous
         * step with the Transaction Data Hash Code retrieved from the ICC
         * Dynamic Data in Step 5. If they are not the same, CDA has failed.
         */
        hexData = "";
        hexData += getEmvTransData().getPdolData();
        LogUtil.d(TAG, "PDOL Data=[" + getEmvTransData().getPdolData() + "]");
        hexData += getEmvTransData().getCdol1Data();
        LogUtil.d(TAG, "CDOL1 Data=[" + getEmvTransData().getCdol1Data() + "]");
        if (!isFirstGAC) {
            hexData += getEmvTransData().getCdol2Data();
            LogUtil.d(TAG, "CDOL2 Data=[" + getEmvTransData().getCdol2Data() + "]");
        }

        String responseData = response.getTlvMap().get(EMVTag.tag77);
        int signedDynamicAppDataIndex = responseData.indexOf(signedDynamicApplicationData);
        // 8 is signedDynamicApplicationData TAG + length
        hexData += responseData.substring(0, signedDynamicAppDataIndex - 8) + responseData.substring(signedDynamicAppDataIndex + signedDynamicApplicationData.length());
        LogUtil.d(TAG, "hexData2=[" + hexData + "]");

        String hashHex = SecurityUtil.calculateSha1(hexData);
        LogUtil.d(TAG, "calculate hash hex=[" + hashHex + "], transDataHashCode hash hex=[" + transDataHashCode + "]");

        if (!transDataHashCode.equals(hashHex)) {
            LogUtil.d(TAG, "Hash not the same, CDA failed.");
            setErrorCode(EMVResultCode.ERR_SIGNED_DYNAMIC_APP_DATA_TRANS_DATA_HASH_WRONG);
            return false;
        }

        return true;
    }

    private void makeCardRiskManagementWasPerform() {
        getEmvTransData().getTsi().markFlag(TSI.FLAG_CARD_RISK_MANAGEMENT_WAS_PERFORMED, true);
    }

    private byte[] getGenerateACCommandData(boolean isFirstGAC) {
        List<DOLBean> dolBeanList;
        if (isFirstGAC) {
            String cdol1 = getEmvTransData().getTagMap().get(EMVTag.tag8C);
            LogUtil.d(TAG, "CDOL1 tag list=[" + cdol1 + "]");
            dolBeanList = DOLUtil.toDOLDataList(cdol1);
        } else {
            String cdol2 = getEmvTransData().getTagMap().get(EMVTag.tag8D);
            LogUtil.d(TAG, "CDOL2 tag list=[" + cdol2 + "]");
            dolBeanList = DOLUtil.toDOLDataList(cdol2);
        }

        Map<String, String> tagMap = getEmvTransData().getTagMap();
        String cdolData = "";
        for (DOLBean dolBean : dolBeanList) {
            if (TerminalTag.tag98.equals(dolBean.getTag())) {
                calculateTCHashValue();
            }

            if (tagMap.containsKey(dolBean.getTag())) {
                cdolData += dolBean.formatValue(tagMap.get(dolBean.getTag()));
            } else {
                cdolData += StringUtil.getNonNullStringLeftPadding("0", dolBean.getLen() * 2);
            }
        }

        if (isFirstGAC) {
            getEmvTransData().setCdol1Data(cdolData);
        } else {
            getEmvTransData().setCdol2Data(cdolData);
        }
        LogUtil.d(TAG, "CDOL data hex=[" + cdolData + "]");
        return StringUtil.hexStr2Bytes(cdolData);
    }

    /**
     * Calculate Transaction Certificate (TC) Hash Value
     * use TDOL
     */
    private void calculateTCHashValue() {
        LogUtil.d(TAG, "calculateTCHashValue");
        List<DOLBean> dolBeanList;

        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag97)) {
            String tdol = getEmvTransData().getTagMap().get(EMVTag.tag97);
            LogUtil.d(TAG, "TDOL tag list=[" + tdol + "]");
            dolBeanList = DOLUtil.toDOLDataList(tdol);
        } else if (getEmvTransData().getSelectAppTerminalParamsMap().containsKey(ParamTag.DEFAULT_TDOL)) {
            String defaultTdol = getEmvTransData().getSelectAppTerminalParamsMap().get(ParamTag.DEFAULT_TDOL);
            LogUtil.d(TAG, "Default TDOL tag list=[" + defaultTdol + "]");
            dolBeanList = DOLUtil.toDOLDataList(defaultTdol);
        } else {
            return;
        }

        Map<String, String> tagMap = getEmvTransData().getTagMap();
        String tdolData = "";
        for (DOLBean dolBean : dolBeanList) {
            if (TerminalTag.tag98.equals(dolBean.getTag())) {
                calculateTCHashValue();
            }

            if (tagMap.containsKey(dolBean.getTag())) {
                tdolData += dolBean.formatValue(tagMap.get(dolBean.getTag()));
            } else {
                tdolData += StringUtil.getNonNullStringLeftPadding("0", dolBean.getLen() * 2);
            }
        }

        String hashHex = SecurityUtil.calculateSha1(StringUtil.byte2HexStr(tdolData.getBytes()));
        LogUtil.d(TAG, "hash Hex=[" + hashHex + "]");
        getEmvTransData().getTagMap().put(EMVTag.tag98, hashHex);
    }

    private boolean isExistTvrFlagTrue(TVR tac, TVR iac) {
        TVR tvr = getEmvTransData().getTvr();
        for (int i = 0; i < 40; i++) {
            if (tvr.isFlagTrue(i) && (tvr.isFlagTrue(i) == tac.isFlagTrue(i) || tvr.isFlagTrue(i) == iac.isFlagTrue(i))) {
                LogUtil.d(TAG, "Bit[" + i + "] is true.");
                return true;
            }
        }

        return false;
    }

    private String getTerminalActionCode(String tag) {
        if (getEmvTransData().getSelectAppTerminalParamsMap().containsKey(tag)) {
            return getEmvTransData().getSelectAppTerminalParamsMap().get(tag);
        }

        /**
         * The existence of each of the Terminal Action Codes is optional. In the absence of
         * any Terminal Action Code, a default value consisting of all bits set to 0 is to be
         * used in its place. However, it is strongly recommended that as a minimum, the
         * Terminal Action Code - Online and Terminal Action Code - Default should be
         * included with the bits corresponding to ‘Offline data authentication was not
         * performed’, and either ‘SDA failed’, or ‘DDA failed’ or ‘CDA failed’ set to 1.15
         */
        if (ParamTag.TAC_DEFAULT.equals(tag)) {
            return "0000000000";
        } else {
            return "CC00000000";
        }
    }

    private String getIssuerActionCode(String tag) {
        if (getEmvTransData().getTagMap().containsKey(tag)) {
            return getEmvTransData().getTagMap().get(tag);
        }

        /**
         * If the Issuer Action Code - Denial does not exist, a default value with all bits set
         * to 0 is to be used.
         *
         * If the Issuer Action Code - Online is not present, a default value with all bits set
         * to 1 shall be used in its place.
         *
         * If the Issuer Action Code - Default does not exist, a default value with all bits set
         * to 1 shall be used in its place.
         */
        if (EMVTag.tag9F0E.equals(tag)) {
            return "0000000000";
        } else {
            return "FFFFFFFFFF";
        }
    }
}
