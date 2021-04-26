package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.emvkernel.data.consts.TransType;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

/**
 * Application Usage Control
 *
 * Application Usage Control Byte 1 (Leftmost)
 * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
 * 1 x x x x x x x Valid for domestic cash transactions
 * x 1 x x x x x x Valid for international cash transactions
 * x x 1 x x x x x Valid for domestic goods
 * x x x 1 x x x x Valid for international goods
 * x x x x 1 x x x Valid for domestic services
 * x x x x x 1 x x Valid for international services
 * x x x x x x 1 x Valid at ATMs
 * x x x x x x x 1 Valid at terminals other than ATMs
 *
 * Application Usage Control Byte 2 (Rightmost)
 * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
 * 1 x x x x x x x Domestic cashback allowed
 * x 1 x x x x x x International cashback allowed
 * x x 0 x x x x x RFU
 * x x x 0 x x x x RFU
 * x x x x 0 x x x RFU
 * x x x x x 0 x x RFU
 * x x x x x x 0 x RFU
 * x x x x x x x 0 RFU
 */
public class AUC {
    private final String TAG = TAGS.EMV_STATE;
    private byte[] auc;

    // byte 1 (b8 -> b1)
    public static final String FLAG_VALID_FOR_DOMESTIC_CASH_TRANSACTION = "FLAG_VALID_FOR_DOMESTIC_CASH_TRANSACTION";
    public static final String FLAG_VALID_FOR_INTERNATIONAL_CASH_TRANSACTIONS = "FLAG_VALID_FOR_INTERNATIONAL_CASH_TRANSACTIONS";
    public static final String FLAG_VALID_FOR_DOMESTIC_GOODS = "FLAG_VALID_FOR_DOMESTIC_GOODS";
    public static final String FLAG_VALID_FOR_INTERNATIONAL_GOODS = "FLAG_VALID_FOR_INTERNATIONAL_GOODS";
    public static final String FLAG_VALID_FOR_DOMESTIC_SERVICES = "FLAG_VALID_FOR_DOMESTIC_SERVICES";
    public static final String FLAG_VALID_FOR_INTERNATIONAL_SERVICES = "FLAG_VALID_FOR_INTERNATIONAL_SERVICES";
    public static final String FLAG_VALID_AT_ATMS = "FLAG_VALID_AT_ATMS";
    public static final String FLAG_VALID_AT_TERMINALS_OTHER_THAN_ATMS = "FLAG_VALID_AT_TERMINALS_OTHER_THAN_ATMS";

    // byte 1 (b8 -> b7)
    public static final String FLAG_DOMESTIC_CASHBACK_ALLOWED = "FLAG_DOMESTIC_CASHBACK_ALLOWED";
    public static final String FLAG_INTERNATIONAL_CASHBACK_ALLOWED = "FLAG_INTERNATIONAL_CASHBACK_ALLOWED";

    public AUC(String hexValue) {
        LogUtil.d(TAG, "Application Usage Control=[" + hexValue + "]");
        hexValue = StringUtil.getNonNullStringLeftPadding(hexValue, 4);
        auc = StringUtil.hexStr2Bytes(hexValue);
    }

    public void clear() {
        Arrays.fill(auc, (byte) 0x00);
    }

    public String getAUCHex() {
        return StringUtil.byte2HexStr(auc);
    }

    public boolean isBitSetOn(String flag) {
        int bytePosition;
        byte bitPosition;
        switch (flag) {
            case FLAG_VALID_FOR_DOMESTIC_CASH_TRANSACTION:
                bytePosition = 0;
                bitPosition = (byte) 0x80;
                break;
            case FLAG_VALID_FOR_INTERNATIONAL_CASH_TRANSACTIONS:
                bytePosition = 0;
                bitPosition = (byte) 0x40;
                break;
            case FLAG_VALID_FOR_DOMESTIC_GOODS:
                bytePosition = 0;
                bitPosition = (byte) 0x20;
                break;
            case FLAG_VALID_FOR_INTERNATIONAL_GOODS:
                bytePosition = 0;
                bitPosition = (byte) 0x10;
                break;
            case FLAG_VALID_FOR_DOMESTIC_SERVICES:
                bytePosition = 0;
                bitPosition = (byte) 0x08;
                break;
            case FLAG_VALID_FOR_INTERNATIONAL_SERVICES:
                bytePosition = 0;
                bitPosition = (byte) 0x04;
                break;
            case FLAG_VALID_AT_ATMS:
                bytePosition = 0;
                bitPosition = (byte) 0x02;
                break;
            case FLAG_VALID_AT_TERMINALS_OTHER_THAN_ATMS:
                bytePosition = 0;
                bitPosition = (byte) 0x01;
                break;
            case FLAG_DOMESTIC_CASHBACK_ALLOWED:
                bytePosition = 1;
                bitPosition = (byte) 0x80;
                break;
            case FLAG_INTERNATIONAL_CASHBACK_ALLOWED:
                bytePosition = 1;
                bitPosition = (byte) 0x40;
                break;
            default:
                return false;
        }

        boolean isBitSetOn = false;
        if ((auc[bytePosition] & bitPosition) > 0) {
            isBitSetOn = true;
        }

        LogUtil.d(TAG, "flag=[" + flag + "] is [" + isBitSetOn + "]");
        return isBitSetOn;
    }

    public boolean isCurrentTransCorrect(String transactionType, boolean isIssuerCountryCodeMatchTerminalCountryCode) {
        switch (transactionType) {
            case TransType.PURCHASE:
                if (isIssuerCountryCodeMatchTerminalCountryCode
                        && (isBitSetOn(FLAG_VALID_FOR_DOMESTIC_SERVICES) || isBitSetOn(FLAG_VALID_FOR_DOMESTIC_GOODS))) {
                    return true;
                } else if (!isIssuerCountryCodeMatchTerminalCountryCode
                        && (isBitSetOn(FLAG_VALID_FOR_INTERNATIONAL_SERVICES) || isBitSetOn(FLAG_VALID_FOR_INTERNATIONAL_GOODS))) {
                    return true;
                }
                break;
            case TransType.CASH:
                if (isIssuerCountryCodeMatchTerminalCountryCode && isBitSetOn(FLAG_VALID_FOR_DOMESTIC_CASH_TRANSACTION)) {
                    return true;
                } else if (!isIssuerCountryCodeMatchTerminalCountryCode && isBitSetOn(FLAG_VALID_FOR_INTERNATIONAL_CASH_TRANSACTIONS)) {
                    return true;
                }
                break;
            case TransType.CASH_BACK:
                if (isIssuerCountryCodeMatchTerminalCountryCode && isBitSetOn(FLAG_DOMESTIC_CASHBACK_ALLOWED)) {
                    return true;
                } else if (!isIssuerCountryCodeMatchTerminalCountryCode && isBitSetOn(FLAG_INTERNATIONAL_CASHBACK_ALLOWED)) {
                    return true;
                }
                break;
        }

        return false;
    }
}
