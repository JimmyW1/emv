package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.libtools.utils.StringUtil;
import com.vfi.android.libtools.utils.TLVUtil;

import java.util.Map;

public class GetDataResponse extends ApduResponse {
    private Map<String, String> tlvMap;

    public GetDataResponse(byte[] response) {
        super(response);

        if (isSuccess()) {
            String hexTags = StringUtil.byte2HexStr(getData());
            tlvMap = TLVUtil.toTlvMap(hexTags);
        }
    }

    @Override
    public void saveTags(Map<String, String> tagMap) {
        if (tlvMap != null && tlvMap.size() > 0) {
            for (String key : tlvMap.keySet()) {
                String tagValue = tlvMap.get(key);
                putTag(tagMap, key, tagValue);
            }
        }
    }

    public Map<String, String> getTlvMap() {
        return tlvMap;
    }
}
