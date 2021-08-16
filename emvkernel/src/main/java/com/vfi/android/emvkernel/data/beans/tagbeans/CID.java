package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

/**
 * Table 14: Coding of Cryptogram Information Data
 *
 * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
 * 0  0                      AAC
 * 0  1                      TC
 * 1  0                      ARQC
 * 1  1                      RFU
 *       x  x                Payment System-specific cryptogram
 *             0             No advice required
 *             1             Advice required
 *                x  x  x    Reason/advice code
 *                0  0  0    No information given
 *                0  0  1    Service not allowed
 *                0  1  0    PIN Try Limit exceeded
 *                0  1  1    Issuer authentication failed
 *                1  x  x    Other values RFU
 */

public class CID {
    private final String TAG = TAGS.EMV_STATE;
    private byte cid;

    public CID(String hexValue) {
        LogUtil.d(TAG, "AIP=[" + hexValue + "]");
        hexValue = StringUtil.getNonNullStringLeftPadding(hexValue, 2);
        cid = StringUtil.hexStr2Bytes(hexValue)[0];
    }

    public void clear() {
        cid = 0x00;
    }

    public String getTSIHex() {
        return String.format("%02X", cid);
    }

    public boolean isAAC() {
        return (cid & 0x80) > 0;
    }

    public boolean isTC() {
        return (cid & 0x40) > 0;
    }
}
