package com.vfi.android.emvkernel.data.beans;

import java.util.ArrayList;
import java.util.List;

public class EmvParams {
    private boolean isContact; // true - insert card; false - tap card.
    private int emvParameterGroup = 0; // default emv parameter group
    private boolean isSupportPSE; // default true;
    private boolean isSupportCardHolderSelect; // default true
    private List<CTLSKernelIndicator> ctlsKernelIndicatorList;
    private String amount; // Authorised amount 9F02
    private String transCurrencyCode; // Transaction Currency Code 5F2A

    /**
     * PBOC parameters
     */
    private boolean isSupportECash; // enable/disable electronic cash   9F7A

    public EmvParams() {
        isSupportPSE = true;
        isSupportCardHolderSelect = true;
        ctlsKernelIndicatorList = new ArrayList<>();
        isSupportECash = false;
    }

    public boolean isContact() {
        return isContact;
    }

    public void setContact(boolean contact) {
        isContact = contact;
    }

    public int getEmvParameterGroup() {
        return emvParameterGroup;
    }

    public void setEmvParameterGroup(int emvParameterGroup) {
        this.emvParameterGroup = emvParameterGroup;
    }

    public boolean isSupportPSE() {
        return isSupportPSE;
    }

    public void setSupportPSE(boolean supportPSE) {
        isSupportPSE = supportPSE;
    }

    public boolean isSupportCardHolderSelect() {
        return isSupportCardHolderSelect;
    }

    public void setSupportCardHolderSelect(boolean supportCardHolderSelect) {
        isSupportCardHolderSelect = supportCardHolderSelect;
    }

    public boolean isSupportECash() {
        return isSupportECash;
    }

    public void setSupportECash(boolean supportECash) {
        isSupportECash = supportECash;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransCurrencyCode() {
        return transCurrencyCode;
    }

    public void setTransCurrencyCode(String transCurrencyCode) {
        this.transCurrencyCode = transCurrencyCode;
    }
}
