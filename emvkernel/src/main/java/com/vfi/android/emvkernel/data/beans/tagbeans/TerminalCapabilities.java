package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

public class TerminalCapabilities {
    private final String TAG = TAGS.EMV_STATE;
    private byte[] terminalCap;

    public TerminalCapabilities(String hexValue) {
        LogUtil.d(TAG, "TerminalCapabilities=[" + hexValue + "]");
        hexValue = StringUtil.getNonNullStringLeftPadding(hexValue, 6);
        terminalCap = StringUtil.hexStr2Bytes(hexValue);
    }

    public void clear() {
        Arrays.fill(terminalCap, (byte) 0x00);
    }

    public String getTSIHex() {
        return StringUtil.byte2HexStr(terminalCap);
    }

    public boolean isSupportCDA() {
        boolean isSupportCDA = (terminalCap[2] & 0x08) > 0;
        LogUtil.d(TAG, "Terminal SupportCDA=[" + isSupportCDA + "]");
        return isSupportCDA;
    }

    public boolean isSupportDDA() {
        boolean isSupportDDA = (terminalCap[2] & 0x40) > 0;
        LogUtil.d(TAG, "Terminal SupportDDA=[" + isSupportDDA + "]");
        return isSupportDDA;
    }

    public boolean isSupportSDA() {
        boolean isSupportSDA = (terminalCap[2] & 0x80) > 0;
        LogUtil.d(TAG, "Terminal SupportSDA=[" + isSupportSDA + "]");
        return isSupportSDA;
    }
}
