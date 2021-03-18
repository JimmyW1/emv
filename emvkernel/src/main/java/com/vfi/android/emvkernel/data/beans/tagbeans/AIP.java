package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

public class AIP {
    private final String TAG = TAGS.EMV_STATE;
    private byte[] aip;

    public AIP(String hexValue) {
        LogUtil.d(TAG, "AIP=[" + hexValue + "]");
        hexValue = StringUtil.getNonNullStringLeftPadding(hexValue, 4);
        aip = StringUtil.hexStr2Bytes(hexValue);
    }

    public void clear() {
        Arrays.fill(aip, (byte) 0x00);
    }

    public String getTSIHex() {
        return StringUtil.byte2HexStr(aip);
    }

    public boolean isSupportCDA() {
        boolean isSupportCDA = (aip[1] & 0x01) > 0;
        LogUtil.d(TAG, "CARD SupportCDA[" + isSupportCDA + "]");
        return isSupportCDA;
    }

    public boolean isSupportDDA() {
        boolean isSupportDDA = (aip[0] & 0x20) > 0;
        LogUtil.d(TAG, "CARD SupportDDA[" + isSupportDDA + "]");
        return isSupportDDA;
    }

    public boolean isSupportSDA() {
        boolean isSupportSDA = (aip[0] & 0x40) > 0;
        LogUtil.d(TAG, "CARD SupportSDA[" + isSupportSDA + "]");
        return isSupportSDA;
    }
}
