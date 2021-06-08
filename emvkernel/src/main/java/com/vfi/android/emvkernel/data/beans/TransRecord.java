package com.vfi.android.emvkernel.data.beans;

public class TransRecord {
    private String pan;
    private String amount;
    private String panSeqNum;
    private String transDate;

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPanSeqNum() {
        return panSeqNum;
    }

    public void setPanSeqNum(String panSeqNum) {
        this.panSeqNum = panSeqNum;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }
}
