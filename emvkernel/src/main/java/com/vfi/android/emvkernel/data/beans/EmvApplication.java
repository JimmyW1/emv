package com.vfi.android.emvkernel.data.beans;

public class EmvApplication {
    private String dfName;

    public EmvApplication(String dfName) {
        this.dfName = dfName;
    }

    public String getDfName() {
        return dfName;
    }
}
