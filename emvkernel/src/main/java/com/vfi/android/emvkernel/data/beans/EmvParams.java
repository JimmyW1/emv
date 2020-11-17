package com.vfi.android.emvkernel.data.beans;

import java.util.ArrayList;
import java.util.List;

public class EmvParams {
    private boolean isContact; // true - insert card; false - tap card.
    private int emvParameterGroup = 0; // default emv parameter group
    private boolean isSupportPSE; // default true;
    private List<CTLSKernelIndicator> ctlsKernelIndicatorList;

    public EmvParams() {
        isSupportPSE = true;
        ctlsKernelIndicatorList = new ArrayList<>();
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
}
