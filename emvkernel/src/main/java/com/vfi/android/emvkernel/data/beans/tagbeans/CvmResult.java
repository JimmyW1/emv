package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.emvkernel.interfaces.Callback;
import com.vfi.android.libtools.utils.StringUtil;

public class CvmResult {
    private byte[] cvmResult = new byte[] {0x3F, 0x00, 0x00};
    private Callback callback;

    public CvmResult(Callback callback) {
        this.callback = callback;
    }

    public void clear() {
        cvmResult = new byte[] {0x3F, 0x00, 0x00};
    }

    public String getTVRHex() {
        return StringUtil.byte2HexStr(cvmResult);
    }
}
