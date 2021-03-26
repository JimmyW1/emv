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
import com.vfi.android.emvkernel.data.consts.ParamTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.emvkernel.utils.SecurityUtil;
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
        if (supportMode == NOT_SUPPORT) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED, true);
            // TODO jump to next state
        }

        if (checkIfMissingMandatoryData(supportMode)) {
            LogUtil.d(TAG, "Offline data authentication missing Mandatory data.");
            getEmvTransData().getTvr().markFlag(TVR.FLAG_ICC_DATA_MISSING, true);
            setErrorCode(EMVResultCode.ERR_MISSING_MANDATORY_DATA);
            stopEmv();
            return;
        }

        if (!retrievalCertificationAuthorityPublicKey()) {
            // TODO should not terminate transaction.
            markTvrOfflineDynamicDataAuthenticationFailed(supportMode);
            // TODO whether need stop emv
            setErrorCode(EMVResultCode.ERR_MISSING_CERT_AUTH_PUBLIC_KEY);
            stopEmv();
        }

        if (!retrievalIssuerPublicKey()) {
            markTvrOfflineDynamicDataAuthenticationFailed(supportMode);
            stopEmv();
        }

        if (supportMode == CDA) {
            doCDAProcess();
        } else if (supportMode == DDA) {
            doDDAProcess();
        } else if (supportMode == SDA) {
            doSDAProcess();
        }
    }

    private void markTvrOfflineDynamicDataAuthenticationFailed(int supportMode) {
        if (supportMode == CDA) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_CDA_FAILED, true);
        } else if (supportMode == DDA) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_DDA_FAILED, true);
        } else if (supportMode == SDA) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_SDA_FAILED, true);
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

    private boolean retrievalCertificationAuthorityPublicKey() {
        String dfName = getEmvTransData().getTagMap().get(EMVTag.tag84);
        String rid = dfName.substring(0, 10);
        String authPubKeyIndex = getEmvTransData().getTagMap().get(EMVTag.tag8F);
        LogUtil.d(TAG, "retrievalCertificationAuthorityPublicKey rid=[" + rid + "] authPubKeyIndex=[" + authPubKeyIndex + "]");

        for (Map<String, String> emvKeyMap : getEmvTransData().getCaPublicKeyList()) {
            if (rid.equals(emvKeyMap.get(ParamTag.RID))
                    && authPubKeyIndex.equals(emvKeyMap.get(ParamTag.AUTH_PUB_KEY_IDX))) {
                LogUtil.d(TAG, "retrievalCertificationAuthorityPublicKey success");
                getEmvTransData().setSelectCardEmvKeyParamsMap(emvKeyMap);
                printDebugCardEmvKeyParams();
                return true;
            }
        }

        LogUtil.d(TAG, "retrievalCertificationAuthorityPublicKey failed");
        return false;
    }

    private boolean retrievalIssuerPublicKey() {
        LogUtil.d(TAG, "retrievalIssuerPublicKey");
        String issuerPublicKeyCert = getEmvTransData().getTagMap().get(EMVTag.tag90);
        String authorityPublicKeyCert = getEmvTransData().getSelectCardEmvKeyParamsMap().get(ParamTag.KEY);
        String issuerPublicKeyRemainder = getEmvTransData().getTagMap().get(EMVTag.tag92);
        String issuerPublicKeyExponent = getEmvTransData().getTagMap().get(EMVTag.tag9F32);
        LogUtil.d(TAG, "issuerPublicKeyCert=[" + issuerPublicKeyCert + "]");
        LogUtil.d(TAG, "authorityPublicKeyCert=[" + authorityPublicKeyCert + "]");
        LogUtil.d(TAG, "issuerPublicKeyRemainder=[" + issuerPublicKeyRemainder + "]");
        LogUtil.d(TAG, "issuerPublicKeyExponent=[" + issuerPublicKeyExponent + "]");

        // 1. Issuer Public Key Certificate has a length different from the length of the Certification Authority Public Key Modulus
        if (issuerPublicKeyCert.length() != authorityPublicKeyCert.length()) {
            LogUtil.d(TAG, "Issuer public key has different length from authority public key");
            setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_LEN_DIFFERENT_FROM_AUTHORITY_PUB_KEY);
            return false;
        }

//        try {
//            SecurityUtil.signVerify(issuerPublicKeyCert, authorityPublicKeyCert);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return true;
    }

    private void printDebugCardEmvKeyParams() {
        LogUtil.d(TAG, "=============printDebugCardEmvKeyParams Start===============");
        for (String tag : getEmvTransData().getSelectCardEmvKeyParamsMap().keySet()) {
            LogUtil.d(TAG, "TAG[" + tag + "]=[" + getEmvTransData().getSelectCardEmvKeyParamsMap().get(tag) + "]");
        }
        LogUtil.d(TAG, "=============printDebugCardEmvKeyParams   End===============");
    }
}
