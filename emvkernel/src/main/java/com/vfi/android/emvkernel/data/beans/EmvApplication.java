package com.vfi.android.emvkernel.data.beans;

public class EmvApplication {
    private String dfName;
    private String label;
    /**
     * b8 b7–b5 b4–b1   Definition
     * 1                Application cannot be selected without confirmation by the cardholder
     * 0                Application may be selected without confirmation by the cardholder
     *    xxx           RFU
     *          0000    No priority assigned
     *          xxxx    (except * 0000) Order in which the application is to be listed or selected, ranging from 1–15, with 1 being highest priority
     */
    private byte appPriorityIndicator; // tag87

    public EmvApplication(String dfName, String label, byte appPriorityIndicator) {
        this.dfName = dfName;
        this.label = label;
        this.appPriorityIndicator = appPriorityIndicator;
    }

    public boolean isAutoSelect() {
        if ((appPriorityIndicator & 0x80) > 0) {
            return false;
        }

        return true;
    }

    public String getDfName() {
        return dfName;
    }

    public byte getAppPriorityIndicator() {
        return appPriorityIndicator;
    }

    public void setAppPriorityIndicator(byte appPriorityIndicator) {
        this.appPriorityIndicator = appPriorityIndicator;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
