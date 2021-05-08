package com.vfi.android.emvkernel.data.beans.tagbeans;

public class CvmRule {
    /**
     * define at {@link com.vfi.android.emvkernel.data.consts.CvmType}
     */
    private byte cvmCode;
    private byte cvmConditionCode;

    public CvmRule(byte cvmCode, byte cvmConditionCode) {
        this.cvmCode = cvmCode;
        this.cvmConditionCode = cvmConditionCode;
    }

    public byte getCvmCode() {
        return cvmCode;
    }

    public byte getCvmConditionCode() {
        return cvmConditionCode;
    }
}
