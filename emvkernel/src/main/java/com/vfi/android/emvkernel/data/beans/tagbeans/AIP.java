package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

/**
 * AIP Byte 1 (Leftmost)
 * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
 * 0 x x x x x x x RFU
 * x 1 x x x x x x SDA supported
 * x x 1 x x x x x DDA supported
 * x x x 1 x x x x Cardholder verification is supported
 * x x x x 1 x x x Terminal risk management is to be performed
 * x x x x x 1 x x Issuer authentication is supported 19
 * x x x x x x 0 x RFU
 * x x x x x x x 1 CDA supported
 *
 * AIP Byte 2 (Rightmost)
 * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
 * 0 x x x x x x x Reserved for use by the EMV
 * Contactless Specifications
 * x 0 x x x x x x RFU
 * x x 0 x x x x x RFU
 * x x x 0 x x x x RFU
 * x x x x 0 x x x RFU
 * x x x x x 0 x x RFU
 * x x x x x x 0 x RFU
 * x x x x x x x 0 RFU
 *
 */
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
        boolean isSupportCDA = (aip[0] & 0x01) > 0;
        LogUtil.d(TAG, "CARD SupportCDA[" + isSupportCDA + "]");
        return isSupportCDA;
    }

    public boolean isOnDeviceCardHolderVerificationNotSupport() {
        boolean isOnDeviceCardHolderVerificationNotSupport = (aip[0] & 0x02) > 0;
        LogUtil.d(TAG, "CARD IsOnDeviceCardHolderVerificationNotSupport[" + isOnDeviceCardHolderVerificationNotSupport + "]");
        return isOnDeviceCardHolderVerificationNotSupport;
    }

    public boolean isSupportGenerateAC() {
        boolean isSupportGenerateAC = (aip[0] & 0x04) > 0;
        LogUtil.d(TAG, "CARD SupportGenerateACCommand[" + isSupportGenerateAC + "]");
        return isSupportGenerateAC;
    }

    public boolean isSupportTerminalRiskManagement() {
        boolean isSupportTerminalRiskManagement = (aip[0] & 0x08) > 0;
        LogUtil.d(TAG, "CARD SupportTerminalRiskManagement[" + isSupportTerminalRiskManagement + "]");
        return isSupportTerminalRiskManagement;
    }

    public boolean isSupportCardHolderVerification() {
        boolean isSupportCardHolderVerification = (aip[0] & 0x10) > 0;
        LogUtil.d(TAG, "CARD SupportCardHolderVerification[" + isSupportCardHolderVerification + "]");
        return isSupportCardHolderVerification;
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
