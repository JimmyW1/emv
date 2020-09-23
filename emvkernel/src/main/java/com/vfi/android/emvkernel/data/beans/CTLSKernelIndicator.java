package com.vfi.android.emvkernel.data.beans;

public class CTLSKernelIndicator {
    private int ctlsKernelId;
    private String RID;

    public CTLSKernelIndicator(String RID, int ctlsKernelId) {
        this.ctlsKernelId = ctlsKernelId;
        this.RID = RID;
    }
}
