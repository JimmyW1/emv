package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.SW12;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;
import com.vfi.android.libtools.utils.TLVUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadRecordResponse extends ApduResponse {

    private boolean isNoRecord;

    private String tag70;
    private List<String> tag61List;
    private Map<String, String> tlvMap;

    public ReadRecordResponse(byte[] response, boolean isPSERecord) {
        super(response);

        if (isSuccess()) {
            String hexFCIStr = StringUtil.byte2HexStr(getData());
            tlvMap = TLVUtil.toTlvMap(hexFCIStr);
            if (tlvMap.containsKey(EMVTag.tag70)) {
                tag70 = tlvMap.get(EMVTag.tag70);
                LogUtil.d(TAG, "tag70=" + tag70);

                if (isPSERecord) {
                    tag61List = TLVUtil.getTagList(tag70, EMVTag.tag61);
                    printDebugInfoTag61List(tag61List);
                } else {
                    tag61List = new ArrayList<>();
                }
            }
        } else if (getStatus() != null) {
            switch (getStatus()) {
                case SW12.ERR_NO_RECORD: // 6A83
                    isNoRecord = true;
                    break;
            }
        } else {

        }
    }

    public boolean isNoRecord() {
        return isNoRecord;
    }

    public List<String> getTag61List() {
        return tag61List;
    }

    @Override
    public void saveTags(Map<String, String> tagMap) {
        if (tlvMap != null && tlvMap.size() > 0) {
            for (String key : tlvMap.keySet()) {
                String tagValue = tlvMap.get(key);
                if (!EMVTag.tag70.equals(key)) {
                    putTag(tagMap, key, tagValue);
                }
            }
        }
    }

    private void printDebugInfoTag61List(List<String> tag61List) {
        for (int i = 0; i < tag61List.size(); i++) {
            Map<String, String> tag61TlvMap = TLVUtil.toTlvMap(tag61List.get(i));
            if (tag61TlvMap.containsKey(EMVTag.tag4F)) {
                String tag4F = tag61TlvMap.get(EMVTag.tag4F);
                LogUtil.d(TAG, "tag4F=" + tag4F);
            }

            if (tag61TlvMap.containsKey(EMVTag.tag50)) {
                String tag50 = tag61TlvMap.get(EMVTag.tag50);
                LogUtil.d(TAG, "tag50=" + tag50);
            }

            if (tag61TlvMap.containsKey(EMVTag.tag9F12)) {
                String tag9F12 = tag61TlvMap.get(EMVTag.tag9F12);
                LogUtil.d(TAG, "tag9F12=" + tag9F12);
            }

            if (tag61TlvMap.containsKey(EMVTag.tag87)) {
                String tag87 = tag61TlvMap.get(EMVTag.tag87);
                LogUtil.d(TAG, "tag87=" + tag87);
            }

            if (tag61TlvMap.containsKey(EMVTag.tag73)) {
                String tag73 = tag61TlvMap.get(EMVTag.tag73);
                LogUtil.d(TAG, "tag73=" + tag73);
            }
        }
    }
}
