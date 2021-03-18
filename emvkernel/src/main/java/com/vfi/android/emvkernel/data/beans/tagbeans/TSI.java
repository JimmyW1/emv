package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

public class TSI {
    byte[] tsi = new byte[2];

    public void clear() {
        Arrays.fill(tsi, (byte) 0x00);
    }

    public String getTSIHex() {
        return StringUtil.byte2HexStr(tsi);
    }
}
