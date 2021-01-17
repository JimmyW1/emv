package com.vfi.android.emvkernel.data.beans;

import com.vfi.android.libtools.utils.StringUtil;

import java.util.Arrays;

public class TVR {
    byte[] tvr = new byte[5];

    public void clear() {
        Arrays.fill(tvr, (byte) 0x00);
    }

    public String getTVRHex() {
        return StringUtil.byte2HexStr(tvr);
    }
}
