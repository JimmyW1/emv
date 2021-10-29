package com.vfi.android.emvkernel.data.beans;

public class OnlineInfo {
    private boolean isNeedSignature;

    public OnlineInfo() {
    }

    public boolean isNeedSignature() {
        return isNeedSignature;
    }

    public void setNeedSignature(boolean needSignature) {
        isNeedSignature = needSignature;
    }
}
