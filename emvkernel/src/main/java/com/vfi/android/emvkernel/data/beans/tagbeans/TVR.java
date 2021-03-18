package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

public class TVR {
    private final String TAG = TAGS.EMV_FLOW;
    public static final String FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED = "FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED";

    byte[] tvr = new byte[5];

    public void clear() {
        Arrays.fill(tvr, (byte) 0x00);
    }

    public String getTVRHex() {
        return StringUtil.byte2HexStr(tvr);
    }

    public void markFlag(String flag, boolean flagValue) {
        LogUtil.d(TAG, "markFlag flag=[" + flag + "] -> [" + flagValue + "]");
        int bytePosition;
        byte bitPosition;
        switch (flag) {
            case FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED:
                bytePosition = 0;
                bitPosition = (byte) 0x80;
                break;
            default:
                return;
        }

        if (flagValue) {
            tvr[bytePosition] |= bitPosition;
        } else {
            tvr[bytePosition] &= ~bitPosition;
        }
    }
}
