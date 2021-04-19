package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.InternalAuthenticateCmd;
import com.vfi.android.emvkernel.corelogical.apdu.InternalAuthenticateResponse;
import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartOfflineDataAuth;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartProcessingRestrictions;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.beans.DOLBean;
import com.vfi.android.emvkernel.data.beans.tagbeans.AIP;
import com.vfi.android.emvkernel.data.beans.tagbeans.TVR;
import com.vfi.android.emvkernel.data.beans.tagbeans.TerminalCapabilities;
import com.vfi.android.emvkernel.data.consts.EMVResultCode;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.ParamTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.emvkernel.utils.DOLUtil;
import com.vfi.android.emvkernel.utils.SecurityUtil;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;
import com.vfi.android.libtools.utils.TLVUtil;

import java.util.List;
import java.util.Map;

public class OfflineDataAuthenticationState extends AbstractEmvState {
    public static final int NOT_SUPPORT = 0;
    public static final int SDA = 1;
    public static final int DDA = 2;
    public static final int CDA = 3;

    private String issuerPublicKey;
    private String iccPublicKey;

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
            finishOfflineDataAuthentication(supportMode, false);
            return;
        }

        if (!retrievalIssuerPublicKey()) {
            finishOfflineDataAuthentication(supportMode, false);
            return;
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
            isMissingMandatoryData = false;
            // The exception may be that the Issuer Public Key Remainder or the ICC Public Key
            // Remainder could be absent. This is because if the public key modulus can be recovered in
            // its entirety from the public key certificate there is no need for a remainder.
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
            isMissingMandatoryData = false;
            // The exception may be that the Issuer Public Key Remainder or the ICC Public Key
            // Remainder could be absent. This is because if the public key modulus can be recovered in
            // its entirety from the public key certificate there is no need for a remainder.
        }

        if ((supportMode == CDA || supportMode == DDA) && !tagMap.containsKey(EMVTag.tag9F49)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 9F49 (Dynamic Data Authentication Data ObjectList (DDOL))");
            isMissingMandatoryData = true;
        }

        LogUtil.d(TAG, "isMissingMandatoryData=[" + isMissingMandatoryData + "]");
        return isMissingMandatoryData;
    }

    private void doSDAProcess() {
        //12. If all the checks above are correct, concatenate the Leftmost Digits of the
        //Issuer Public Key and the Issuer Public Key Remainder (if present) to
        //obtain the Issuer Public Key Modulus, and continue with the next steps
        //for the verification of the Signed Static Application Data.

        //1. If the Signed Static Application Data has a length different from the
        //length of the Issuer Public Key Modulus, SDA has failed.
        String signedStaticApplicationData = getEmvTransData().getTagMap().get(EMVTag.tag93);
        LogUtil.d(TAG, "Signed Static Application Data=[" + signedStaticApplicationData + "]");
        if (issuerPublicKey == null || issuerPublicKey.length() != signedStaticApplicationData.length()) {
            LogUtil.d(TAG, "Signed Static Application Data has a length different from the length of the Issuer Public Key Modulus");
            setErrorCode(EMVResultCode.ERR_SIGNED_STATIC_APP_DATA_HAVE_DIFFERENT_LENGTH_WITH_PUBLIC_KEY);
            finishOfflineDataAuthentication(SDA, false);
            return;
        }

        byte[] signedStaticApplicationDataBytes = StringUtil.hexStr2Bytes(signedStaticApplicationData);
        byte[] issuerPublicKeyModules = StringUtil.hexStr2Bytes(issuerPublicKey);
        byte[] issuerPublicKeyExponent = StringUtil.hexStr2Bytes(getEmvTransData().getTagMap().get(EMVTag.tag9F32));
        byte[] recoveredSignedStaticAppDataBytes = SecurityUtil.signVerify(signedStaticApplicationDataBytes, issuerPublicKeyExponent, issuerPublicKeyModules);
        String recoveredSignedStaticAppDataHex = StringUtil.byte2HexStr(recoveredSignedStaticAppDataBytes);
        LogUtil.d(TAG, "recoveredSignedStaticAppDataHex=[" + recoveredSignedStaticAppDataHex + "]");

        if (recoveredSignedStaticAppDataHex == null || recoveredSignedStaticAppDataHex.length() < 42 * 2) {
            LogUtil.d(TAG, "Recovered sign static app data failed.");
            setErrorCode(EMVResultCode.ERR_SIGNED_STATIC_APP_DATA_RECOVERED_FAILED);
            finishOfflineDataAuthentication(SDA, false);
            return;
        }

        //2.If the Recovered Data Trailer is not equal to
        //'BC', offline dynamic data authentication has failed.
        if (!recoveredSignedStaticAppDataHex.endsWith("BC")) {
            LogUtil.d(TAG, "Recovered Data Trailer is not equal to 'BC'");
            setErrorCode(EMVResultCode.ERR_SIGNED_STATIC_APP_DATA_TRAILER_NOT_BC);
            finishOfflineDataAuthentication(SDA, false);
            return;
        }

        //3. Check the Recovered Data Header. If it is not '6A', offline dynamic data
        //authentication has failed.
        if (!recoveredSignedStaticAppDataHex.startsWith("6A")) {
            LogUtil.d(TAG, "Recovered Data Header is not equal to '6A'");
            setErrorCode(EMVResultCode.ERR_SIGNED_STATIC_APP_DATA_HEADER_NOT_6A);
            finishOfflineDataAuthentication(SDA, false);
            return;
        }

        //4. Check the Certificate Format. If it is not '04', offline dynamic data
        //authentication has failed.
        if (!recoveredSignedStaticAppDataHex.substring(2, 4).equals("03")) {
            LogUtil.d(TAG, "Recovered Data Certificate Format not equal to '03'");
            setErrorCode(EMVResultCode.ERR_SIGNED_STATIC_APP_DATA_CERTIFICATE_FORMAT_NOT_03);
            finishOfflineDataAuthentication(SDA, false);
            return;
        }

        //5. Concatenate from left to right the second to the fifth data elements in
        //Table 7 (that is, Signed Data Format through Pad Pattern), followed by
        //the static data to be authenticated as specified in section 10.3 of Book 3. If
        //the Static Data Authentication Tag List is present and contains tags other
        //than '82', then SDA has failed.
        //6. Apply the indicated hash algorithm (derived from the Hash Algorithm
        //Indicator) to the result of the concatenation of the previous step to
        //produce the hash result.
        //7. Compare the calculated hash result from the previous step with the
        //recovered Hash Result. If they are not the same, SDA has failed.
        String hashData = "";
        hashData += recoveredSignedStaticAppDataHex.substring(2, recoveredSignedStaticAppDataHex.length() - 2 - 40);
        LogUtil.d(TAG, "hashData=[" + hashData + "]");

        if (getEmvTransData().isExistStaticDataRecordNotCodeWithTag70()) {
            LogUtil.d(TAG, "Read offline static data record not code with tag70");
            setErrorCode(EMVResultCode.ERR_READ_OFFLINE_STATIC_DATA_RECORD_NOT_CODED_WITH_TAG70);
            finishOfflineDataAuthentication(SDA, false);
            return;
        }

        hashData += getEmvTransData().getStaticDataToBeAuthenticated();
        LogUtil.d(TAG, "hashData=[" + hashData + "]");

        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag9F4A)) {
            String tag9F4AValue = getEmvTransData().getTagMap().get(EMVTag.tag9F4A);
            LogUtil.d(TAG, "Optional Static Data Authentication Tag List=[" + tag9F4AValue + "]");
            if (tag9F4AValue.length() > 0 && !tag9F4AValue.equals(EMVTag.tag82)) {
                LogUtil.d(TAG, "Optional Static Data Authentication Tag List not only tag82");
                setErrorCode(EMVResultCode.ERR_OPTIONAL_STATIC_DATA_AUTHENTICATION_TAG_LIST_NOT_ONLY_TAG82);
                finishOfflineDataAuthentication(SDA, false);
                return;
            }
            hashData += getEmvTransData().getTagMap().get(EMVTag.tag82);
            LogUtil.d(TAG, "hashData=[" + hashData + "]");
        }

        String hash = SecurityUtil.calculateSha1(hashData);
        LogUtil.d(TAG, "hash=[" + hash + "]");
        int recoveredHashStartIndex = recoveredSignedStaticAppDataHex.length() - 2 - 40;
        String recoveredHashHex = recoveredSignedStaticAppDataHex.substring(recoveredHashStartIndex, recoveredHashStartIndex + 40);
        if (!hash.equals(recoveredHashHex)) {
            LogUtil.d(TAG, "Calculate hash is not the same with recovered hash result");
            setErrorCode(EMVResultCode.ERR_CALCULATE_HASH_IS_NOT_THE_SAME_WITH_RECOVERED_HASH_RESULT);
            finishOfflineDataAuthentication(SDA, false);
            return;
        }

        finishOfflineDataAuthentication(SDA, true);
        LogUtil.d(TAG, "Perform SDA Success");

        int dataAuthCodeStartIndex = (1 + 1 + 1) * 2;
        String dataAuthenticationCode = recoveredSignedStaticAppDataHex.substring(dataAuthCodeStartIndex, dataAuthCodeStartIndex + 2 * 2);
        LogUtil.d(TAG, "dataAuthenticationCode=[" + dataAuthenticationCode + "]");
        getEmvTransData().getTagMap().put(EMVTag.tag9F45, dataAuthenticationCode);
    }

    private void doCDAProcess() {

    }

    private void doDDAProcess() {
        String ddolTags = getDDOL();
        if (ddolTags == null || ddolTags.length() <= 0) {
            LogUtil.d(TAG, "DDA Missing DDOL");
            setErrorCode(EMVResultCode.ERR_MISSING_DDOL);
            finishOfflineDataAuthentication(DDA, false);
            return;
        }

        boolean isExistUnpredictableNumber = false;
        String ddolTagsData = "";
        if (ddolTags != null && ddolTags.length() > 0) {
            List<DOLBean> dolBeanList = DOLUtil.toDOLDataList(ddolTags);
            for (DOLBean dolBean : dolBeanList) {
                if (dolBean.getTag().equals(EMVTag.tag9F37) && dolBean.getLen() == 4) {
                    isExistUnpredictableNumber = true;
                }
                if (getEmvTransData().getTagMap().containsKey(dolBean.getTag())) {
                    ddolTagsData += dolBean.formatValue(getEmvTransData().getTagMap().get(dolBean.getTag()));
                } else {
                    ddolTagsData += StringUtil.getNonNullStringLeftPadding("0", dolBean.getLen() * 2);
                }
            }
        }

        if (!isExistUnpredictableNumber) {
            LogUtil.d(TAG, "Missging tag9F37 - Unpredictable Number");
            setErrorCode(EMVResultCode.ERR_MISSING_UNPREDICTABLE_NUMBER);
            finishOfflineDataAuthentication(DDA, false);
            return;
        }

        //InternalAuthenticate
        byte[] ret = executeApduCmd(new InternalAuthenticateCmd(ddolTagsData));
        InternalAuthenticateResponse response = new InternalAuthenticateResponse(ret);
        if (!response.isSuccess()) {
            LogUtil.d(TAG, "Execute InternalAuthenticate failed. response status=[" + response.getStatus() + "]");
            setErrorCode(EMVResultCode.ERR_EXECUTE_INTERNAL_AUTHENTICATE_FAILED);
            finishOfflineDataAuthentication(DDA, false);
            return;
        }

        if (!retrievalICCPublicKey()) {

        }

        byte[] iccPublicKeyExponent = StringUtil.hexStr2Bytes(getEmvTransData().getTagMap().get(EMVTag.tag9F47));

    }

    private String getDDOL() {
        String DDOL;
        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag9F49)) {
            DDOL = getEmvTransData().getTagMap().get(EMVTag.tag9F49);
            LogUtil.d(TAG, "ICC DDOL=[" + DDOL + "]");
        } else {
            DDOL = getEmvTransData().getSelectAppTerminalParamsMap().get(ParamTag.DEFAULT_DDOL);
            LogUtil.d(TAG, "Terminal deafult DDOL=[" + DDOL + "]");
        }

        return DDOL;
    }

    private void finishOfflineDataAuthentication(int supportMode, boolean isSuccess) {
        if (isSuccess) {

        } else {
            markTvrOfflineDynamicDataAuthenticationFailed(supportMode);
            jumpToState(EmvStateType.STATE_PROCESSING_RESTRICTIONS);
            sendMessage(new Msg_StartProcessingRestrictions());
        }
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

    private  boolean retrievalICCPublicKey() {
        LogUtil.d(TAG, "retrievalICCPublicKey");
        String iccPublicKeyCert = getEmvTransData().getTagMap().get(EMVTag.tag9F46);
        String iccPublicKeyExponent = getEmvTransData().getTagMap().get(EMVTag.tag9F47);
        String iccPublicKeyRemainder = getEmvTransData().getTagMap().get(EMVTag.tag9F48);
        LogUtil.d(TAG, "iccPublicKeyCert=[" + iccPublicKeyCert + "]");
        LogUtil.d(TAG, "iccPublicKeyExponent=[" + iccPublicKeyExponent + "]");
        LogUtil.d(TAG, "iccPublicKeyRemainder=[" + iccPublicKeyRemainder + "]");

        // 1. If the ICC Public Key Certificate has a length different from the length of
        //the Issuer Public Key Modulus obtained in the previous section, offline
        //dynamic data authentication has failed.
        if (issuerPublicKey.length() != iccPublicKeyCert.length()) {
            LogUtil.d(TAG, "ICC Public Key Certificate has a length different from issuer public key");
            setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_LEN_DIFFERENT_FROM_ICC_PUBLIC_KEY_CERT);
            return false;
        }

        byte[] iccPublicKeyCertBytes = StringUtil.hexStr2Bytes(iccPublicKeyCert);
        byte[] issuerPublicKeyExponentBytes = StringUtil.hexStr2Bytes(getEmvTransData().getTagMap().get(EMVTag.tag9F32));
        byte[] iccPublicKeyCertRecoveredBytes = SecurityUtil.signVerify(iccPublicKeyCertBytes, issuerPublicKeyExponentBytes, StringUtil.hexStr2Bytes(issuerPublicKey));
        String iccPublicKeyCertRecoveredHex = StringUtil.byte2HexStr(iccPublicKeyCertRecoveredBytes);
        LogUtil.d(TAG, "iccPublicKeyCertRecoveredHex=[" + iccPublicKeyCertRecoveredHex + "]");

        // 2. In order to obtain the recovered data specified in Table 14, apply the
        //recovery function as specified in Annex A2.1 on the ICC Public Key
        //Certificate using the Issuer Public Key in conjunction with the
        //corresponding algorithm. If the Recovered Data Trailer is not equal to
        //'BC', offline dynamic data authentication has failed

        if (iccPublicKeyCertRecoveredHex == null || iccPublicKeyCertRecoveredHex.length() < 42 * 2) {
            LogUtil.d(TAG, "Recovered data failed.");
            setErrorCode(EMVResultCode.ERR_ICC_PUB_KEY_RECOVERED_FAILED);
            return false;
        }

        if (!iccPublicKeyCertRecoveredHex.endsWith("BC")) {
            LogUtil.d(TAG, "Recovered Data Trailer is not equal to 'BC'");
            setErrorCode(EMVResultCode.ERR_ICC_PUB_KEY_RECOVERED_DATA_TRAILER_NOT_BC);
            return false;
        }

        //3. Check the Recovered Data Header. If it is not '6A', offline dynamic data
        //authentication has failed.
        if (!iccPublicKeyCertRecoveredHex.startsWith("6A")) {
            LogUtil.d(TAG, "Recovered Data Header is not equal to '6A'");
            setErrorCode(EMVResultCode.ERR_ICC_PUB_KEY_RECOVERED_DATA_HEADER_NOT_6A);
            return false;
        }

        //4. Check the Certificate Format. If it is not '02', offline dynamic data
        //authentication has failed.
        if (!iccPublicKeyCertRecoveredHex.substring(2, 4).equals("04")) {
            LogUtil.d(TAG, "Recovered Data Certificate Format not equal to '04'");
            setErrorCode(EMVResultCode.ERR_ICC_PUB_KEY_RECOVERED_DATA_CERTIFICATE_FORMAT_NOT_04);
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
        String hexData = iccPublicKeyCertRecoveredHex.substring(2, iccPublicKeyCertRecoveredHex.length() - 2 - 40);
        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag9F48)) {
            hexData += getEmvTransData().getTagMap().get(EMVTag.tag9F48);
        }
        hexData += getEmvTransData().getTagMap().get(EMVTag.tag9F47);

        if (getEmvTransData().isExistStaticDataRecordNotCodeWithTag70()) {
            LogUtil.d(TAG, "Read offline static data record not code with tag70");
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
        String hashIndicator = iccPublicKeyCertRecoveredHex.substring(hashIndicatorStartIndex, hashIndicatorStartIndex + 2);
        LogUtil.d(TAG, "hashIndicator=[" + hashIndicator + "]");
        if (hashIndicator.equals("01")) {
            String hashHex = SecurityUtil.calculateSha1(hexData);
            String iccKeyCertificateHash = iccPublicKeyCertRecoveredHex.substring(iccPublicKeyCertRecoveredHex.length() - 2 - 40, iccPublicKeyCertRecoveredHex.length() - 2);
            LogUtil.d(TAG, "calculate hash hex=[" + hashHex + "], certificate hash hex=[" + iccKeyCertificateHash + "]");

            if (!iccKeyCertificateHash.equals(hashHex)) {
                LogUtil.d(TAG, "Hash not the same, DDA failed.");
                setErrorCode(EMVResultCode.ERR_ICC_PUB_KEY_RECOVERED_DATA_CERTIFICATE_HASH_WRONG);
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
        String applicationPanHex = iccPublicKeyCertRecoveredHex.substring(applicationPanStartIndex, applicationPanStartIndex + 10 * 2);
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
        String certificateExpirationDate = iccPublicKeyCertRecoveredHex.substring(certificateExpirationDateStartIndex, certificateExpirationDateStartIndex + 2 * 2);
        String year = certificateExpirationDate.substring(2);
        String MM = certificateExpirationDate.substring(0, 2);
        if (StringUtil.parseInt(year, 0) <= 50) {
            year = "20" + year;
        } else {
            year = "19" + year;
        }
        certificateExpirationDate = year + MM + StringUtil.getLastDayOfThisMonth();
        String currentYYYYMMDD = StringUtil.getSystemDate();
        LogUtil.d(TAG, "currentYYYYMMDD=[" + currentYYYYMMDD + "] issuerPublicKeyExpirationDate=[" + certificateExpirationDate + "]");
        if (StringUtil.parseInt(currentYYYYMMDD, 0) >= StringUtil.parseInt(certificateExpirationDate, 0)) {
            LogUtil.d(TAG, "Public key certificate expiration");
            setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_EXPIRATION);
            return false;
        }

        //10. If the ICC Public Key Algorithm Indicator is not recognised, offline
        //dynamic data authentication has failed.
        int publicKeyAlgIndicatorStartIndex = (1+1+10+2+3+1) * 2;
        String publicKeyAlgIndicator = iccPublicKeyCertRecoveredHex.substring(publicKeyAlgIndicatorStartIndex, publicKeyAlgIndicatorStartIndex+2);
        if (!publicKeyAlgIndicator.equals("01")) {
            LogUtil.d(TAG, "Public key Algorithm Indicator is not recognised");
            setErrorCode(EMVResultCode.ERR_PUBLIC_KEY_ALG_INDICATOR_IS_NOT_RECOGNISED);
            return false;
        }

        int iccPublicKeyLenStartIndex = (1+1+10+2+3+1+1) * 2;
        String iccPublicKeyLenHex = iccPublicKeyCertRecoveredHex.substring(iccPublicKeyLenStartIndex, iccPublicKeyLenStartIndex+2);
        int iccPublicKeyLen = StringUtil.parseInt(iccPublicKeyLenHex, 16,0);
        // hex len need double
        iccPublicKeyLen = iccPublicKeyLen * 2;
        LogUtil.d(TAG, "iccPublicKeyLen=[" + iccPublicKeyLen + "]");

        int iccPublicKeyRemainderLen = iccPublicKeyRemainder == null ? 0 : iccPublicKeyRemainder.length();
        LogUtil.d(TAG, "iccPublicKeyRemainderLen=[" + iccPublicKeyRemainderLen + "]");

        int iccPublicKeyStartIndex = (1+1+10+2+3+1+1+1+1) * 2;
        int iccPublicKeyLeftMostDigitLen = iccPublicKeyLen - iccPublicKeyRemainderLen;
        iccPublicKey = iccPublicKeyCertRecoveredHex.substring(iccPublicKeyStartIndex, iccPublicKeyStartIndex + iccPublicKeyLeftMostDigitLen);
        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag9F48)) {
            iccPublicKey += getEmvTransData().getTagMap().get(EMVTag.tag9F48);
        }
        LogUtil.d(TAG, "iccPublicKey=[" + iccPublicKey + "]");

        LogUtil.d(TAG, "retrievalICCPublicKey success");
        return true;
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

        // 2. In order to obtain the recovered data specified in Table 6, apply the
        //recovery function specified in Annex A2.1 to the Issuer Public Key
        //Certificate using the Certification Authority Public Key in conjunction
        //with the corresponding algorithm. If the Recovered Data Trailer is not
        //equal to 'BC', SDA has failed.
        byte[] authorPublicKey = StringUtil.hexStr2Bytes(getEmvTransData().getSelectCardEmvKeyParamsMap().get(ParamTag.KEY));
        byte[] exponent = StringUtil.hexStr2Bytes(getEmvTransData().getSelectCardEmvKeyParamsMap().get(ParamTag.EXPONENT));
        byte[] certifiedIssuerPublicKey = StringUtil.hexStr2Bytes(getEmvTransData().getTagMap().get(EMVTag.tag90));
        byte[] recoverDataBytes = SecurityUtil.signVerify(certifiedIssuerPublicKey, exponent, authorPublicKey);
        String recoverDataBytesHex = StringUtil.byte2HexStr(recoverDataBytes);
        LogUtil.d(TAG, "recoverDataBytesHex=[" + recoverDataBytesHex  + "]");

        if (recoverDataBytesHex == null || recoverDataBytesHex.length() < 36 * 2) {
            LogUtil.d(TAG, "Recovered data failed.");
            setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_RECOVERED_FAILED);
            return false;
        }

        if (!recoverDataBytesHex.endsWith("BC")) {
            LogUtil.d(TAG, "Recovered Data Trailer is not equal to 'BC'");
            setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_RECOVERED_DATA_TRAILER_NOT_BC);
            return false;
        }

        // 3. Check the Recovered Data Header. If it is not '6A', SDA has failed.
        if (!recoverDataBytesHex.startsWith("6A")) {
            LogUtil.d(TAG, "Recovered Data Header is not equal to '6A'");
            setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_RECOVERED_DATA_HEADER_NOT_6A);
            return false;
        }

        // 4. Check the Certificate Format. If it is not '02', SDA has failed.
        if (!recoverDataBytesHex.substring(2, 4).equals("02")) {
            LogUtil.d(TAG, "Recovered Data Certificate Format not equal to '02'");
            setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_RECOVERED_DATA_CERTIFICATE_FORMAT_NOT_02);
            return false;
        }

        //5. Concatenate from left to right the second to the tenth data elements in
        //Table 6 (that is, Certificate Format through Issuer Public Key or Leftmost
        //Digits of the Issuer Public Key), followed by the Issuer Public Key
        //Remainder (if present), and finally the Issuer Public Key Exponent.
        //6. Apply the indicated hash algorithm (derived from the Hash Algorithm
        //Indicator) to the result of the concatenation of the previous step to
        //produce the hash result.
        //7. Compare the calculated hash result from the previous step with the
        //recovered Hash Result. If they are not the same, SDA has failed.
        String hexData = recoverDataBytesHex.substring(2, recoverDataBytesHex.length() - 2 - 40);
        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag92)) {
            hexData += getEmvTransData().getTagMap().get(EMVTag.tag92);
        }
        hexData += getEmvTransData().getTagMap().get(EMVTag.tag9F32);
        LogUtil.d(TAG, "hexData=[" + hexData + "]");
        int hashIndicatorStartIndex = (1+1+4+2+3) * 2;
        String hashIndicator = recoverDataBytesHex.substring(hashIndicatorStartIndex, hashIndicatorStartIndex + 2);
        LogUtil.d(TAG, "hashIndicator=[" + hashIndicator + "]");
        if (hashIndicator.equals("01")) {
            String hashHex = SecurityUtil.calculateSha1(hexData);
            String publicKeyCertificateHash = recoverDataBytesHex.substring(recoverDataBytesHex.length() - 2 - 40, recoverDataBytesHex.length() - 2);
            LogUtil.d(TAG, "calculate hash hex=[" + hashHex + "], certificate hash hex=[" + publicKeyCertificateHash + "]");

            if (!publicKeyCertificateHash.equals(hashHex)) {
                LogUtil.d(TAG, "Hash not the same, SDA failed.");
                setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_RECOVERED_DATA_CERTIFICATE_HASH_WRONG);
                return false;
            }
        } else {
            LogUtil.d(TAG, "Hash indicator =[" + hashIndicator + "] not support");
            setErrorCode(EMVResultCode.ERR_HASH_INDICATOR_ALGO_NOT_SUPPORT);
            return false;
        }

        //8. Verify that the Issuer Identifier matches the leftmost 3-8 PAN digits
        //(allowing for the possible padding of the Issuer Identifier with
        //hexadecimal 'F's). If not, SDA has failed.
        int issuerIdentifierStartIndex = (1+1) * 2;
        String issuerIdentifier = recoverDataBytesHex.substring(issuerIdentifierStartIndex, issuerIdentifierStartIndex + 4 * 2);
        issuerIdentifier = issuerIdentifier.replace("F", "");
        String panHex = getEmvTransData().getTagMap().get(EMVTag.tag5A);
        if (!panHex.startsWith(issuerIdentifier)) {
            LogUtil.d(TAG, "Verify Issuer Identifier failed.");
            setErrorCode(EMVResultCode.ERR_VERIFY_ISSUER_IDENTIFIER_FAILED);
            return false;
        }

        //9. Verify that the last day of the month specified in the Certificate
        //Expiration Date is equal to or later than today’s date. If the Certificate
        //Expiration Date is earlier than today’s date, the certificate has expired, in
        //which case SDA has failed.
        int certificateExpirationDateStartIndex = (1+1+4) * 2;
        // format MMYY
        String certificateExpirationDate = recoverDataBytesHex.substring(certificateExpirationDateStartIndex, certificateExpirationDateStartIndex + 2 * 2);
        String year = certificateExpirationDate.substring(2);
        String MM = certificateExpirationDate.substring(0, 2);
        if (StringUtil.parseInt(year, 0) <= 50) {
            year = "20" + year;
        } else {
            year = "19" + year;
        }
        certificateExpirationDate = year + MM + StringUtil.getLastDayOfThisMonth();
        String currentYYYYMMDD = StringUtil.getSystemDate();
        LogUtil.d(TAG, "currentYYYYMMDD=[" + currentYYYYMMDD + "] issuerPublicKeyExpirationDate=[" + certificateExpirationDate + "]");
        if (StringUtil.parseInt(currentYYYYMMDD, 0) >= StringUtil.parseInt(certificateExpirationDate, 0)) {
            LogUtil.d(TAG, "Public key certificate expiration");
            setErrorCode(EMVResultCode.ERR_ISSUER_PUB_KEY_EXPIRATION);
            return false;
        }

        //10. Verify that the concatenation of RID, Certification Authority Public Key
        //Index, and Certificate Serial Number is valid. If not, SDA has failed.
        // TODO how to check Certificate Serial Number is valid

        //11. If the Issuer Public Key Algorithm Indicator is not recognised, SDA has
        //failed.
        int publicKeyAlgIndicatorStartIndex = (1+1+4+2+3+1) * 2;
        String publicKeyAlgIndicator = recoverDataBytesHex.substring(publicKeyAlgIndicatorStartIndex, publicKeyAlgIndicatorStartIndex+2);
        if (!publicKeyAlgIndicator.equals("01")) {
            LogUtil.d(TAG, "Public key Algorithm Indicator is not recognised");
            setErrorCode(EMVResultCode.ERR_PUBLIC_KEY_ALG_INDICATOR_IS_NOT_RECOGNISED);
            return false;
        }

        int issuerPublicKeyLenStartIndex = (1+1+4+2+3+1+1) * 2;
        String issuerPublicKeyLenHex = recoverDataBytesHex.substring(issuerPublicKeyLenStartIndex, issuerPublicKeyLenStartIndex+2);
        int issuerPublicKeyLen = StringUtil.parseInt(issuerPublicKeyLenHex, 16,0);
        // hex len need double
        issuerPublicKeyLen = issuerPublicKeyLen * 2;
        LogUtil.d(TAG, "issuerPublicKeyLen=[" + issuerPublicKeyLen + "]");

        int issuerPublicKeyRemainderLen = issuerPublicKeyRemainder == null ? 0 : issuerPublicKeyRemainder.length();
        LogUtil.d(TAG, "issuerPublicKeyRemainderLen=[" + issuerPublicKeyRemainderLen + "]");

        int issuerPublicKeyStartIndex = (1+1+4+2+3+1+1+1+1) * 2;
        int issuerPublicKeyLeftMostDigitLen = issuerPublicKeyLen - issuerPublicKeyRemainderLen;
        issuerPublicKey = recoverDataBytesHex.substring(issuerPublicKeyStartIndex, issuerPublicKeyStartIndex + issuerPublicKeyLeftMostDigitLen);
        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag92)) {
            issuerPublicKey += getEmvTransData().getTagMap().get(EMVTag.tag92);
        }
        LogUtil.d(TAG, "issuerPublicKey=[" + issuerPublicKey + "]");

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
