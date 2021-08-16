package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.emvkernel.interfaces.Callback;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

public class TSI {
    private final String TAG = TAGS.EMV_FLOW;
    private byte[] tsi = new byte[2];
    private Callback callback;

    // byte 1 (b8 -> b3)
    public static final String FLAG_OFFLINE_DATA_AUTH_WAS_PERFORMED = "FLAG_OFFLINE_DATA_AUTH_WAS_PERFORMED";
    public static final String FLAG_CARDHOLDER_VERIFICATION_WAS_PERFORMED = "FLAG_CARDHOLDER_VERIFICATION_WAS_PERFORMED";
    public static final String FLAG_CARD_RISK_MANAGEMENT_WAS_PERFORMED = "FLAG_CARD_RISK_MANAGEMENT_WAS_PERFORMED";
    public static final String FLAG_ISSUER_AUTHENTICATION_WAS_PERFORMED = "FLAG_ISSUER_AUTHENTICATION_WAS_PERFORMED";
    public static final String FLAG_TERMINAL_RISK_MANAGEMENT_WAS_PERFORMED = "FLAG_TERMINAL_RISK_MANAGEMENT_WAS_PERFORMED";
    public static final String FLAG_SCRIPT_PROCESSING_WAS_PERFORMED = "FLAG_SCRIPT_PROCESSING_WAS_PERFORMED";

    public TSI(Callback callback) {
        this.callback = callback;
    }

    public void clear() {
        Arrays.fill(tsi, (byte) 0x00);
    }

    public String getTSIHex() {
        return StringUtil.byte2HexStr(tsi);
    }

    public void markFlag(String flag, boolean flagValue) {
        LogUtil.d(TAG, "markFlag flag=[" + flag + "] -> [" + flagValue + "]");
        int bytePosition;
        byte bitPosition;
        switch (flag) {
            case FLAG_OFFLINE_DATA_AUTH_WAS_PERFORMED:
                bytePosition = 0;
                bitPosition = (byte) 0x80;
                break;
            case FLAG_CARDHOLDER_VERIFICATION_WAS_PERFORMED:
                bytePosition = 0;
                bitPosition = (byte) 0x40;
                break;
            case FLAG_CARD_RISK_MANAGEMENT_WAS_PERFORMED:
                bytePosition = 0;
                bitPosition = (byte) 0x20;
                break;
            case FLAG_ISSUER_AUTHENTICATION_WAS_PERFORMED:
                bytePosition = 0;
                bitPosition = (byte) 0x10;
                break;
            case FLAG_TERMINAL_RISK_MANAGEMENT_WAS_PERFORMED:
                bytePosition = 0;
                bitPosition = (byte) 0x08;
                break;
            case FLAG_SCRIPT_PROCESSING_WAS_PERFORMED:
                bytePosition = 0;
                bitPosition = (byte) 0x04;
                break;
            default:
                return;
        }

        if (flagValue) {
            tsi[bytePosition] |= bitPosition;
        } else {
            tsi[bytePosition] &= ~bitPosition;
        }

        if (callback != null) {
            callback.onDataChanged(getTSIHex());
        }
    }
}
