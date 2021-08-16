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
import com.vfi.android.emvkernel.data.beans.tagbeans.CvmResult;
import com.vfi.android.emvkernel.data.beans.tagbeans.TSI;
import com.vfi.android.emvkernel.data.beans.tagbeans.TVR;
import com.vfi.android.emvkernel.data.consts.EMVResultCode;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.ParamTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.emvkernel.utils.DOLUtil;
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
        String tacDefault = getTerminalActionCode(ParamTag.TAC_DEFAULT);
        String tacOnline = getTerminalActionCode(ParamTag.TAC_ONLINE);
        String tacDenial = getTerminalActionCode(ParamTag.TAC_DENIAL);
        LogUtil.d(TAG, "tacDefault=[" + tacDefault + "]");
        LogUtil.d(TAG, "tacOnline=[" + tacOnline + "]");
        LogUtil.d(TAG, "tacDenial=[" + tacDenial + "]");

        String iacDefault = getIssuerActionCode(EMVTag.tag9F0D);
        String iacOnline = getIssuerActionCode(EMVTag.tag9F0F);
        String iacDenial = getIssuerActionCode(EMVTag.tag9F0E);
        LogUtil.d(TAG, "iacDefault=[" + iacDefault + "]");
        LogUtil.d(TAG, "iacOnline=[" + iacOnline + "]");
        LogUtil.d(TAG, "iacDenial=[" + iacDenial + "]");

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
            performAAC(isRequireCDA);
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
                performAAC(isRequireCDA);
            } else {
                performTC(isRequireCDA);
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
                performARQC(isRequireCDA);
            } else {
                performTC(isRequireCDA);
            }
        }

        makeCardRiskManagementWasPerform();
    }

    private void performAAC(boolean isRequireCDA) {
        LogUtil.d(TAG, "TAA result AAC.");
        // rejected transaction offline
        byte[] ret = executeApduCmd(new GenerateApplicationCryptogramCmd(GenerateApplicationCryptogramCmd.TYPE_AAC, isRequireCDA, getGenerateACCommandData(true)));
        GenerateApplicationCryptogramResponse response = new GenerateApplicationCryptogramResponse(ret);
        if (response.isSuccess()) {
            setErrorCode(EMVResultCode.ERR_TAA_RESULT_AAC);
            getEmvHandler().onTransactionResult(new EmvResultInfo(true, getEmvTransData().getErrorCode()));
            finishEmv();
            return;
        } else {

        }

        return;
    }

    private void performARQC(boolean isRequireCDA) {
        LogUtil.d(TAG, "TAA result ARQC.");
        // transaction online
        byte[] ret = executeApduCmd(new GenerateApplicationCryptogramCmd(GenerateApplicationCryptogramCmd.TYPE_ARQC, isRequireCDA, getGenerateACCommandData(true)));
        GenerateApplicationCryptogramResponse response = new GenerateApplicationCryptogramResponse(ret);
        if (response.isSuccess()) {

            return;
        } else {

        }
    }

    private void performTC(boolean isRequireCDA) {
        LogUtil.d(TAG, "TAA result TC.");
        // transaction offline approval
        byte[] ret = executeApduCmd(new GenerateApplicationCryptogramCmd(GenerateApplicationCryptogramCmd.TYPE_TC, isRequireCDA, getGenerateACCommandData(true)));
        GenerateApplicationCryptogramResponse response = new GenerateApplicationCryptogramResponse(ret);
        if (response.isSuccess()) {

            return;
        } else {

        }

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

        LogUtil.d(TAG, "CDOL data hex=[" + cdolData + "]");
        return StringUtil.hexStr2Bytes(cdolData);
    }

    /**
     * Calculate Transaction Certificate (TC) Hash Value
     * use TDOL
     */
    private void calculateTCHashValue() {

    }

    private boolean isExistTvrFlagTrue(TVR tac, TVR iac) {
        TVR tvr = getEmvTransData().getTvr();
        for (int i = 0; i < 40; i++) {
            if (tvr.isFlagTrue(i) == tac.isFlagTrue(i) || tvr.isFlagTrue(i) == iac.isFlagTrue(i)) {
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
