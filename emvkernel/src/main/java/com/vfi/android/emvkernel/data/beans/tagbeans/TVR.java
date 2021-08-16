package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.emvkernel.interfaces.Callback;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

public class TVR {
    private final String TAG = TAGS.EMV_FLOW;
    private byte[] tvr = new byte[5];
    private Callback callback;

    // byte 1
    public static final String FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED = "FLAG_OFFLINE_DATA_AUTH_WAS_NOT_PERFORMED";
    public static final String FLAG_SDA_FAILED = "FLAG_SDA_FAILED";
    public static final String FLAG_ICC_DATA_MISSING = "FLAG_ICC_DATA_MISSING";
    public static final String FLAG_CARD_EXIST_ON_EXCEPTION_FILE_22 = "FLAG_CARD_EXIST_ON_EXCEPTION_FILE_22";
    public static final String FLAG_DDA_FAILED = "FLAG_DDA_FAILED";
    public static final String FLAG_CDA_FAILED = "FLAG_CDA_FAILED";
    // byte 2
    public static final String FLAG_ICC_TERMINAL_HAVE_DIFFERENT_APP_VERSION = "FLAG_ICC_TERMINAL_HAVE_DIFFERENT_APP_VERSION";
    public static final String FLAG_EXPIRED_APPLICATION = "FLAG_EXPIRED_APPLICATION";
    public static final String FLAG_APPLICATION_NOT_YET_EFFECTIVE = "FLAG_APPLICATION_NOT_YET_EFFECTIVE";
    public static final String FLAG_REQUEST_SERVICE_NOT_ALLOW_FOR_CARD_PRODUCT = "FLAG_REQUEST_SERVICE_NOT_ALLOW_FOR_CARD_PRODUCT";
    public static final String FLAG_NEW_CARD = "FLAG_NEW_CARD";
    // byte 3
    public static final String FLAG_CARDHOLDER_VERIFICATION_WAS_NOT_SUCCESSFUL = "FLAG_CARDHOLDER_VERIFICATION_WAS_NOT_SUCCESSFUL";
    public static final String FLAG_UNRECOGNISED_CVM = "FLAG_UNRECOGNISED_CVM";
    public static final String FLAG_PIN_TRY_LIMIT_EXCEEDED = "FLAG_PIN_TRY_LIMIT_EXCEEDED";
    public static final String FLAG_PIN_REQ_PINPAD_NOT_PRESENT_OR_NOT_WORKING = "FLAG_PIN_REQ_PINPAD_NOT_PRESENT_OR_NOT_WORKING";
    public static final String FLAG_PIN_REQ_PINPAD_NOT_PRESENT_PIN_WAS_NOT_ENTERED = "FLAG_PIN_REQ_PINPAD_NOT_PRESENT_PIN_WAS_NOT_ENTERED";
    public static final String FLAG_ONLINE_PIN_ENTERED = "FLAG_ONLINE_PIN_ENTERED";
    // byte 4
    public static final String FLAG_TRANSACTION_EXCEEDS_FLOOR_LIMIT = "FLAG_TRANSACTION_EXCEEDS_FLOOR_LIMIT";
    public static final String FLAG_LOWER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED = "FLAG_LOWER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED";
    public static final String FLAG_UPPER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED = "FLAG_UPPER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED";
    public static final String FLAG_TRANSACTION_SELECTED_RANDOMLY_FOR_ONLINE_PROCESSING = "FLAG_TRANSACTION_SELECTED_RANDOMLY_FOR_ONLINE_PROCESSING";
    public static final String FLAG_MERCHANT_FORCED_TRANSACTION_ONLINE = "FLAG_MERCHANT_FORCED_TRANSACTION_ONLINE";
    // byte 5
    public static final String FLAG_DEFAULT_TDOL_USED = "FLAG_DEFAULT_TDOL_USED";
    public static final String FLAG_ISSUER_AUTHENTICATION_FAILED = "FLAG_ISSUER_AUTHENTICATION_FAILED";
    public static final String FLAG_SCRIPT_PROCESSING_FAILED_BEFORE_FINAL_GAC = "FLAG_SCRIPT_PROCESSING_FAILED_BEFORE_FINAL_GAC";
    public static final String FLAG_SCRIPT_PROCESSING_FAILED_AFTER_FINAL_GAC = "FLAG_SCRIPT_PROCESSING_FAILED_AFTER_FINAL_GAC";

    public TVR(Callback callback) {
        this.callback = callback;
    }

    public TVR(String actionCodeHex) {
        actionCodeHex = StringUtil.getNonNullStringRightPadding(actionCodeHex, 10);
        tvr = StringUtil.hexStr2Bytes(actionCodeHex);
    }

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
            case FLAG_SDA_FAILED:
                bytePosition = 0;
                bitPosition = (byte) 0x40;
                break;
            case FLAG_ICC_DATA_MISSING:
                bytePosition = 0;
                bitPosition = (byte) 0x20;
                break;
            case FLAG_CARD_EXIST_ON_EXCEPTION_FILE_22:
                bytePosition = 0;
                bitPosition = (byte) 0x10;
                break;
            case FLAG_DDA_FAILED:
                bytePosition = 0;
                bitPosition = (byte) 0x08;
                break;
            case FLAG_CDA_FAILED:
                bytePosition = 0;
                bitPosition = (byte) 0x04;
                break;
            case FLAG_ICC_TERMINAL_HAVE_DIFFERENT_APP_VERSION:
                bytePosition = 1;
                bitPosition = (byte) 0x80;
                break;
            case FLAG_EXPIRED_APPLICATION:
                bytePosition = 1;
                bitPosition = (byte) 0x40;
                break;
            case FLAG_APPLICATION_NOT_YET_EFFECTIVE:
                bytePosition = 1;
                bitPosition = (byte) 0x20;
                break;
            case FLAG_REQUEST_SERVICE_NOT_ALLOW_FOR_CARD_PRODUCT:
                bytePosition = 1;
                bitPosition = (byte) 0x10;
                break;
            case FLAG_NEW_CARD:
                bytePosition = 1;
                bitPosition = (byte) 0x08;
                break;
            case FLAG_CARDHOLDER_VERIFICATION_WAS_NOT_SUCCESSFUL:
                bytePosition = 2;
                bitPosition = (byte) 0x80;
                break;
            case FLAG_UNRECOGNISED_CVM:
                bytePosition = 2;
                bitPosition = (byte) 0x40;
                break;
            case FLAG_PIN_TRY_LIMIT_EXCEEDED:
                bytePosition = 2;
                bitPosition = (byte) 0x20;
                break;
            case FLAG_PIN_REQ_PINPAD_NOT_PRESENT_OR_NOT_WORKING:
                bytePosition = 2;
                bitPosition = (byte) 0x10;
                break;
            case FLAG_PIN_REQ_PINPAD_NOT_PRESENT_PIN_WAS_NOT_ENTERED:
                bytePosition = 2;
                bitPosition = (byte) 0x08;
                break;
            case FLAG_ONLINE_PIN_ENTERED:
                bytePosition = 2;
                bitPosition = (byte) 0x04;
                break;
            case FLAG_TRANSACTION_EXCEEDS_FLOOR_LIMIT:
                bytePosition = 3;
                bitPosition = (byte) 0x80;
                break;
            case FLAG_LOWER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED:
                bytePosition = 3;
                bitPosition = (byte) 0x40;
                break;
            case FLAG_UPPER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED:
                bytePosition = 3;
                bitPosition = (byte) 0x20;
                break;
            case FLAG_TRANSACTION_SELECTED_RANDOMLY_FOR_ONLINE_PROCESSING:
                bytePosition = 3;
                bitPosition = (byte) 0x10;
                break;
            case FLAG_MERCHANT_FORCED_TRANSACTION_ONLINE:
                bytePosition = 3;
                bitPosition = (byte) 0x08;
                break;
            case FLAG_DEFAULT_TDOL_USED:
                bytePosition = 4;
                bitPosition = (byte) 0x80;
                break;
            case FLAG_ISSUER_AUTHENTICATION_FAILED:
                bytePosition = 4;
                bitPosition = (byte) 0x40;
                break;
            case FLAG_SCRIPT_PROCESSING_FAILED_BEFORE_FINAL_GAC:
                bytePosition = 4;
                bitPosition = (byte) 0x20;
                break;
            case FLAG_SCRIPT_PROCESSING_FAILED_AFTER_FINAL_GAC:
                bytePosition = 4;
                bitPosition = (byte) 0x10;
                break;
            default:
                return;
        }

        if (flagValue) {
            tvr[bytePosition] |= bitPosition;
        } else {
            tvr[bytePosition] &= ~bitPosition;
        }

        if (callback != null) {
            callback.onDataChanged(getTVRHex());
        }
    }

    public boolean isFlagTrue(int bit) {
        if (bit < 0 || bit >= tvr.length * 8) {
            return false;
        }

        int bytePosition = bit / 8;
        byte bitPosition = (byte) (bit % 8);

        return (tvr[bytePosition] & (0x01 << bitPosition)) > 0;
    }
}
