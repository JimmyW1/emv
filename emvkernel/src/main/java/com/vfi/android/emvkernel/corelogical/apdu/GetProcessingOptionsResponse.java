package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;
import com.vfi.android.emvkernel.data.consts.EMVResultCode;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;
import com.vfi.android.libtools.utils.TLVUtil;

import java.util.Map;

public class GetProcessingOptionsResponse extends ApduResponse {
    private String AIP;
    private String AFL;

    public GetProcessingOptionsResponse(byte[] response) {
        super(response);

        if (isSuccess()) {
            String dataHex = StringUtil.byte2HexStr(getData());
            Map<String, String> tlvMap = TLVUtil.toTlvMap(dataHex);
            if (tlvMap.containsKey(EMVTag.tag80)) {
                int AIPLen = 2;
                String AIP_AFL = tlvMap.get(EMVTag.tag80);
                if (AIP_AFL.length() > AIPLen) {
                    AIP = AIP_AFL.substring(0, AIPLen*2);
                    AFL = AIP_AFL.substring(AIPLen*2);
                    LogUtil.d(TAG, "AIP=[" + AIP + "]");
                    LogUtil.d(TAG, "AFL=[" + AFL + "]");
                } else {
                    setSuccess(false);
                    LogUtil.d(TAG, "Error: Missing AIP AFL [" + AIP_AFL + "]");
                    setErrorCode(EMVResultCode.ERR_MISSING_AIP_AFL);
                }
            } else if (tlvMap.containsKey("77")) {
                if (!tlvMap.containsKey(EMVTag.tag82) || !tlvMap.containsKey(EMVTag.tag94)) {
                    setSuccess(false);
                    LogUtil.d(TAG, "Error: TAG77 Missing AIP AFL");
                    setErrorCode(EMVResultCode.ERR_MISSING_AIP_AFL);
                } else {
                    LogUtil.d(TAG, "AIP=[" + EMVTag.tag82 + "]");
                    LogUtil.d(TAG, "AFL=[" + EMVTag.tag94 + "]");
                    AIP = tlvMap.get(EMVTag.tag82);
                    AFL = tlvMap.get(EMVTag.tag94);
                }
            } else {
                setSuccess(false);
                LogUtil.d(TAG, "Error: Wrong GPO Response=[" + dataHex +"]");
                setErrorCode(EMVResultCode.ERR_GPO_FAILED);
            }
        } else {
            LogUtil.d(TAG, "Error: GPO failed.[" + getStatus() +"]");
            setErrorCode(EMVResultCode.ERR_GPO_FAILED);
        }
    }

    @Override
    public void saveTags(Map<String, String> tagMap) {
        putTag(tagMap, EMVTag.tag82, AIP);
        putTag(tagMap, EMVTag.tag94, AFL);
    }

    public String getAIP() {
        return AIP;
    }

    public String getAFL() {
        return AFL;
    }
}
