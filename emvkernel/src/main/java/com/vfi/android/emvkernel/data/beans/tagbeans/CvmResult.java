package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.emvkernel.interfaces.Callback;
import com.vfi.android.libtools.utils.StringUtil;

public class CvmResult {
    private byte[] cvmResult = new byte[] {0x3F, 0x00, 0x00};
    private Callback callback;

    public static final byte UNKNOWN = 0x00;
    public static final byte FAILED = 0x01;
    public static final byte SUCCESSFUL = 0x02;

    public CvmResult(Callback callback) {
        this.callback = callback;
    }

    /**
     * Book 4, page 49
     * cvmResult
     * '0' = Unknown (for example, for signature)
     * '1' = Failed (for example, for offline PIN)
     * '2' = Successful (for example, for offline PIN)
     */
    public void setCvmResult(byte cvmPerformed, byte cvmCondition, byte cvmResult) {
        this.cvmResult[0] = cvmPerformed;
        this.cvmResult[1] = cvmCondition;
        this.cvmResult[2] = cvmResult;

        if (callback != null) {
            callback.onDataChanged(getCvmResultHex());
        }
    }

    public void clear() {
        cvmResult = new byte[] {0x3F, 0x00, 0x00};
    }

    public String getCvmResultHex() {
        return StringUtil.byte2HexStr(cvmResult);
    }
}
