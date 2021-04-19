package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;
import com.vfi.android.libtools.utils.TLVUtil;

import java.util.Map;

public class InternalAuthenticateResponse extends ApduResponse {
    private Map<String, String> tlvMap;
    private String tag9F4B; // Signed Dynamic Application Data

    public InternalAuthenticateResponse(byte[] response) {
        super(response);

        if (isSuccess()) {
            tlvMap = TLVUtil.toTlvMap(StringUtil.byte2HexStr(getData()));
            if (tlvMap.containsKey(EMVTag.tag77)) {
                tag9F4B = tlvMap.get(EMVTag.tag9F4B);
            } else if (tlvMap.containsKey(EMVTag.tag80)) {
                tag9F4B = tlvMap.get(EMVTag.tag80);
            } else {
                // should never be here
                tag9F4B = "";
                setSuccess(false);
            }
            LogUtil.d(TAG, "tag9F4B=[" + tag9F4B + "]");
        }
    }

    public String getTag9F4B() {
        if (tag9F4B == null) {
            return "";
        }
        return tag9F4B;
    }

    @Override
    public void saveTags(Map<String, String> tagMap) {
        putTag(tagMap, EMVTag.tag9F4B, tag9F4B);
    }
}
