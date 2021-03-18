package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartOfflineDataAuth;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.beans.tagbeans.AIP;
import com.vfi.android.emvkernel.data.beans.tagbeans.TVR;
import com.vfi.android.emvkernel.data.beans.tagbeans.TerminalCapabilities;
import com.vfi.android.emvkernel.data.consts.EMVResultCode;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.libtools.utils.LogUtil;

import java.util.Map;

public class OfflineDataAuthenticationState extends AbstractEmvState {
    public static final int NOT_SUPPORT = 0;
    public static final int SDA = 1;
    public static final int DDA = 2;
    public static final int CDA = 3;

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
        int supportMode = checkSupportOfflineDataAuthMethod();

        if (checkIfMissingMandatoryData(supportMode)) {
            LogUtil.d(TAG, "Offline data authentication missing Mandatory data.");
            setErrorCode(EMVResultCode.ERR_MISSING_MANDATORY_DATA);
            stopEmv();
            return;
        }

        if (supportMode == NOT_SUPPORT) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED, true);
            // TODO jump to next state
        } else if (supportMode == CDA) {
            doCDAProcess();
        } else if (supportMode == DDA) {
            doDDAProcess();
        } else if (supportMode == SDA) {
            doSDAProcess();
        }
    }

    private int checkSupportOfflineDataAuthMethod() {
        int supportMode = NOT_SUPPORT;

        AIP aip= new AIP(getEmvTransData().getTagMap().get(EMVTag.tag82));
        TerminalCapabilities terminalCap = new TerminalCapabilities(getEmvTransData().getTagMap().get(TerminalTag.tag9F33));

        if (aip.isSupportCDA() && terminalCap.isSupportCDA()) {
            supportMode = CDA;
        } else if (aip.isSupportDDA() && terminalCap.isSupportDDA()) {
            supportMode = DDA;
        } else if (aip.isSupportSDA() && terminalCap.isSupportSDA()) {
            supportMode = SDA;
        }

        LogUtil.d(TAG, "checkSupportOfflineDataAuthMethod supportMode=[" + toSupportModeString(supportMode) + "]");
        return supportMode;
    }

    private String toSupportModeString(int supportMode) {
        switch (supportMode) {
            case CDA:
                return "CDA";
            case SDA:
                return "SDA";
            case DDA:
                return "DDA";
            default:
                return "NOT_SUPPORT";
        }
    }

    /**
     * When any mandatory data object is missing, the terminal terminates the
     * transaction unless otherwise specified in these specifications.
     */
    private boolean checkIfMissingMandatoryData(int supportMode) {
        boolean isMissingMandatoryData = false;

        if (supportMode == NOT_SUPPORT) {
            return false;
        }

        Map<String, String> tagMap = getEmvTransData().getTagMap();
        if (!tagMap.containsKey(EMVTag.tag8F)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 8F (Certification Authority Public Key Index)");
            isMissingMandatoryData = true;
        }

        if (!tagMap.containsKey(EMVTag.tag90)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 90 (Issuer Public Key Certificate)");
            isMissingMandatoryData = true;
        }

        if (supportMode == SDA && !tagMap.containsKey(EMVTag.tag93)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 93 (Signed Static Application Data)");
            isMissingMandatoryData = true;
        }

        if (!tagMap.containsKey(EMVTag.tag92)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 92 (Issuer Public Key Remainder)");
            isMissingMandatoryData = true;
        }

        if (!tagMap.containsKey(EMVTag.tag9F32)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 9F32 (Issuer Public Key Exponent)");
            isMissingMandatoryData = true;
        }

        if ((supportMode == CDA || supportMode == DDA) && !tagMap.containsKey(EMVTag.tag9F46)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 9F46 (ICC Public Key Certificate)");
            isMissingMandatoryData = true;
        }

        if ((supportMode == CDA || supportMode == DDA) && !tagMap.containsKey(EMVTag.tag9F47)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 9F47 (ICC Public Key Exponent)");
            isMissingMandatoryData = true;
        }

        if ((supportMode == CDA || supportMode == DDA) && !tagMap.containsKey(EMVTag.tag9F48)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 9F48 (ICC Public Key Remainder)");
            isMissingMandatoryData = true;
        }

        if ((supportMode == CDA || supportMode == DDA) && !tagMap.containsKey(EMVTag.tag9F49)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 9F49 (Dynamic Data Authentication Data ObjectList (DDOL))");
            isMissingMandatoryData = true;
        }

        LogUtil.d(TAG, "isMissingMandatoryData=[" + isMissingMandatoryData + "]");
        return isMissingMandatoryData;
    }

    private void doSDAProcess() {

    }

    private void doCDAProcess() {

    }

    private void doDDAProcess() {

    }
}
