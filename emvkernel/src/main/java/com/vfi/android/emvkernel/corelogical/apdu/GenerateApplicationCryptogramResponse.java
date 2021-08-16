package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;
import com.vfi.android.libtools.utils.TLVUtil;

import java.util.Map;

public class GenerateApplicationCryptogramResponse extends ApduResponse {
    private Map<String, String> tlvMap;
    private boolean isMissingMandatoryData;
    private String cid;
    private String atc;
    private String ac;
    private String issuerApplicationData;

    public GenerateApplicationCryptogramResponse(byte[] response) {
        super(response);

        if (isSuccess()) {
            tlvMap = TLVUtil.toTlvMap(StringUtil.byte2HexStr(getData()));
            if (tlvMap.containsKey(EMVTag.tag77)) {
                String dataHex = tlvMap.get(EMVTag.tag77);
                LogUtil.d(TAG, "Tag77 data hex=[" + dataHex + "]");
                if (dataHex.length() < 22) {
                    LogUtil.d(TAG, "Missing Mandatory field.");
                    isMissingMandatoryData = true;
                    setSuccess(false);
                    return;
                }

                cid = dataHex.substring(0, 2);
                atc = dataHex.substring(2, 6);
                ac = dataHex.substring(6, 22);

                if (dataHex.length() > 22) {
                    issuerApplicationData = dataHex.substring(22);
                }
            } else if (tlvMap.containsKey(EMVTag.tag80)) {
                if (!tlvMap.containsKey(EMVTag.tag9F27) || !tlvMap.containsKey(EMVTag.tag9F13) || !tlvMap.containsKey(EMVTag.tag9F26)) {
                    LogUtil.d(TAG, "Missing Mandatory field.");
                    isMissingMandatoryData = true;
                    setSuccess(false);
                    return;
                }

                cid = tlvMap.get(EMVTag.tag9F27);
                atc = tlvMap.get(EMVTag.tag9F13);
                ac = tlvMap.get(EMVTag.tag9F26);

                if (tlvMap.containsKey(EMVTag.tag9F10)) {
                    issuerApplicationData = tlvMap.get(EMVTag.tag9F10);
                }
            } else {
                // should never be here
                setSuccess(false);
            }

            LogUtil.d(TAG, "COD=[" + cid + "]");
            LogUtil.d(TAG, "ATC=[" + atc + "]");
            LogUtil.d(TAG, "Application Cryptogram=[" + ac + "]");
            LogUtil.d(TAG, "IssuerApplicationData=[" + issuerApplicationData + "]");
        }
    }

    @Override
    public void saveTags(Map<String, String> tagMap) {
        putTag(tagMap, EMVTag.tag9F27, cid);
        putTag(tagMap, EMVTag.tag9F13, atc);
        putTag(tagMap, EMVTag.tag9F26, ac);
        if (issuerApplicationData != null && issuerApplicationData.length() > 0) {
            putTag(tagMap, EMVTag.tag9F10, issuerApplicationData);
        }
    }

    public Map<String, String> getTlvMap() {
        return tlvMap;
    }

    public boolean isMissingMandatoryData() {
        return isMissingMandatoryData;
    }
}
