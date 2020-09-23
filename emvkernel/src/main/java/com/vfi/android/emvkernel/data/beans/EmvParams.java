package com.vfi.android.emvkernel.data.beans;

import java.util.ArrayList;
import java.util.List;

public class EmvParams {
    private boolean isSupportPSE; // default true;
    private List<CTLSKernelIndicator> ctlsKernelIndicatorList;

    public EmvParams() {
        isSupportPSE = true;
        ctlsKernelIndicatorList = new ArrayList<>();
    }
}
