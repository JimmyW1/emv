package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

/**
 * Byte 1: Card Data Input Capability
 * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
 * 1 x x x x x x x Manual key entry
 * x 1 x x x x x x Magnetic stripe
 * x x 1 x x x x x IC with contacts
 * x x x 0 x x x x RFU
 * x x x x 0 x x x RFU
 * x x x x x 0 x x RFU
 * x x x x x x 0 x RFU
 * x x x x x x x 0 RFU
 *
 * Byte 2: CVM Capability
 * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
 * 1 x x x x x x x Plaintext PIN for ICC verification
 * x 1 x x x x x x Enciphered PIN for online verification
 * x x 1 x x x x x Signature (paper)
 * x x x 1 x x x x Enciphered PIN for offline verification
 * x x x x 1 x x x No CVM Required
 * x x x x x 0 x x RFU
 * x x x x x x 0 x RFU
 * x x x x x x x 0 RFU
 *
 * Byte 3: Security Capability
 * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
 * 1 x x x x x x x SDA
 * x 1 x x x x x x DDA
 * x x 1 x x x x x Card capture
 * x x x 0 x x x x RFU
 * x x x x 1 x x x CDA
 * x x x x x 0 x x RFU
 * x x x x x x 0 x RFU
 * x x x x x x x 0 RFU
 */
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

    // byte 2=========================================
    public boolean isSupportPlainTextPinVerifyByICC() {
        boolean isSupportPlainTextPinVerifyByICC = (terminalCap[1] & 0x80) > 0;
        LogUtil.d(TAG, "Terminal isSupportPlainTextPinVerifyByICC=[" + isSupportPlainTextPinVerifyByICC + "]");
        return isSupportPlainTextPinVerifyByICC;
    }

    public boolean isSupportEncipheredPINForOnlineVerification() {
        boolean isSupportEncipheredPINForOnlineVerification = (terminalCap[1] & 0x40) > 0;
        LogUtil.d(TAG, "Terminal isSupportEncipheredPINForOnlineVerification=[" + isSupportEncipheredPINForOnlineVerification + "]");
        return isSupportEncipheredPINForOnlineVerification;
    }

    public boolean isSupportSignaturePaper() {
        boolean isSupportSignaturePaper = (terminalCap[1] & 0x20) > 0;
        LogUtil.d(TAG, "Terminal isSupportSignaturePaper=[" + isSupportSignaturePaper + "]");
        return isSupportSignaturePaper;
    }

    public boolean isSupportEncipheredPINForOfflineVerification() {
        boolean isSupportEncipheredPINForOfflineVerification = (terminalCap[1] & 0x10) > 0;
        LogUtil.d(TAG, "Terminal isSupportEncipheredPINForOfflineVerification=[" + isSupportEncipheredPINForOfflineVerification + "]");
        return isSupportEncipheredPINForOfflineVerification;
    }

    public boolean isSupportNoCVM() {
        boolean isSupportNoCVM = (terminalCap[1] & 0x08) > 0;
        LogUtil.d(TAG, "Terminal isSupportNoCVM=[" + isSupportNoCVM + "]");
        return isSupportNoCVM;
    }
    // byte 2=========================================

    // byte 3=========================================
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
    // byte 3=========================================
}
