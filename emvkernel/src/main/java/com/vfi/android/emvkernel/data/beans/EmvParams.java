package com.vfi.android.emvkernel.data.beans;

import com.vfi.android.emvkernel.data.consts.CDAMode;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class EmvParams {
    private String TAG = TAGS.EMV_COMM;

    private boolean isContact; // true - insert card; false - tap card.
    private int emvParameterGroup = 0; // default emv parameter group
    private boolean isSupportPSE; // default true;
    private boolean isSupportCardHolderSelect; // default true
    private List<CTLSKernelIndicator> ctlsKernelIndicatorList;
    private String amount; // Authorised amount 9F02
    private String otherAmount; // Amount Other(Numeric) 9F03
    private String transCurrencyCode; // Transaction Currency Code 5F2A
    /**
     * Processing code  {@link com.vfi.android.emvkernel.data.consts.TransType}
     * The following is a table specifying the message type and processing code for each transaction type.
     *
     * Transaction	    Message type	Processing code
     * Authorization	0100	        00 a0 0x
     * Balance inquiry	0100            31 a0 0x
     * Sale	            0200	        00 a0 0x
     * Cash	            0200            01 a0 0x
     * Credit Voucher	0200            20 a0 0x
     * Void	            0200            02 a0 0x
     * Mobile topup	                    57 a0 0x
     */
    private String transProcessCode; // ISO 8583:1987 Processing Code
    private String terminalCountryCode; // Terminal country code
    private String terminalType; // Terminal Type 9F35
    /**
     * This is used for response tags in confirm card info {@link CardInfo}
     */
    private List<String> cardConfirmTagList;
    /**
     * if pin bypass will bypass all following cvm rules
     * default true
     */
    private boolean bypassAll;

    /**
     * PBOC parameters
     */
    private boolean isSupportECash; // enable/disable electronic cash   9F7A

    private int cdaMode;

    public EmvParams() {
        isSupportPSE = true;
        isSupportCardHolderSelect = true;
        ctlsKernelIndicatorList = new ArrayList<>();
        isSupportECash = false;
        bypassAll = true; // EMV book 4 page 47
        cdaMode = CDAMode.MODE2; // default do CDA on first GAC
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

    public List<String> getCardConfirmTagList() {
        return cardConfirmTagList;
    }

    public void setCardConfirmTagList(List<String> cardConfirmTagList) {
        this.cardConfirmTagList = cardConfirmTagList;
    }

    public String getTransProcessCode() {
        if (transProcessCode == null || transProcessCode.length() != 2) {
            transProcessCode = StringUtil.getNonNullStringRightPadding(transProcessCode, 2);
        }
        return transProcessCode;
    }

    public void setTransProcessCode(String transProcessCode) {
        this.transProcessCode = transProcessCode;
    }

    public String getTerminalCountryCode() {
        return terminalCountryCode;
    }

    public void setTerminalCountryCode(String terminalCountryCode) {
        this.terminalCountryCode = terminalCountryCode;
    }

    public boolean isBypassAll() {
        LogUtil.d(TAG, "isBypassAll=[" + bypassAll + "]");
        return bypassAll;
    }

    public void setBypassAll(boolean bypassAll) {
        this.bypassAll = bypassAll;
    }

    public int getCdaMode() {
        return cdaMode;
    }

    public void setCdaMode(int cdaMode) {
        this.cdaMode = cdaMode;
    }

    public String getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(String otherAmount) {
        this.otherAmount = otherAmount;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }
}
