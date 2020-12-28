package com.vfi.android.emvkernel.data.beans;

public class EmvApplication {
    private String aidName;

    public EmvApplication(String aidName) {
        this.aidName = aidName;
    }

    public String getAidName() {
        return aidName;
    }
}
