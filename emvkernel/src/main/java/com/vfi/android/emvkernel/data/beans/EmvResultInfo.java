package com.vfi.android.emvkernel.data.beans;

public class EmvResultInfo {
    private boolean isReject;
    /**
     * {@link com.vfi.android.emvkernel.data.consts.EMVResultCode}
     */
    private int resultCode;

    public EmvResultInfo(boolean isReject, int resultCode) {
        this.isReject = isReject;
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public boolean isReject() {
        return isReject;
    }

    public void setReject(boolean reject) {
        isReject = reject;
    }

    @Override
    public String toString() {
        return "EmvResultInfo{" +
                "isReject=" + isReject +
                ", resultCode=" + resultCode +
                '}';
    }
}
