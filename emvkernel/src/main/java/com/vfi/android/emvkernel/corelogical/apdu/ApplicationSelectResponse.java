package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;
import com.vfi.android.emvkernel.data.consts.EMVResultCode;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.SW12;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;
import com.vfi.android.libtools.utils.TLVUtil;

import java.util.Map;

public class ApplicationSelectResponse extends ApduResponse {
    private String tag6F; // FCI Template (M)
    private String tag84; // DF Name (M)
    private String tagA5; // FCI Proprietary Template (M)
    private String tag88; // SFI of the Directory Elementary File (M)
    private String tag5F2D; // Language Preference (O)
    private String tag9F11; // Issuer Code Table Index (O)
    private String tagBF0C; // FCI Issuer Discretionary Data (O)
    private String tag50; // Application Label (O)
    private String tag87; // Application Priority Indicator (O)
    private String tag9F38; // PDOL (O)
    private String tag9F12; // Application Preferred Name (O)
    private String tag9F4D; // Log Entry (O)

    private boolean isPSESelect;
    private boolean isDDFSelect;
    private boolean isADFSelect;

    private boolean isNeedTerminate;

    public ApplicationSelectResponse(byte[] response) {
        super(response);

        if (isSuccess()) {
            printFCIDebugInfo();
        } else if (getStatus() != null) {
            /**
             * If the card is blocked or the SELECT command is not supported (both conditions represented by SW1 SW2 = '6A81'),
             * the terminal terminates the session.
             */
            isNeedTerminate = false;
            switch (getStatus()) {
                case SW12.ERR_CARD_BLOCKED: // 6A81
                    isNeedTerminate = true;
                    setErrorCode(EMVResultCode.ERR_CARD_BLOCKED);
                    LogUtil.e(TAG, "ERROR: Card blocked");
                    break;
                case SW12.ERR_DF_NOT_FOUND: // 6A82
                    LogUtil.d(TAG, "ERROR: No PSE found");
                    break;
                case SW12.ERR_DF_BLOCKED: // 6283, if asi is part match need continue.
                    setErrorCode(EMVResultCode.ERR_APPLICATION_BLOCKED);
                    LogUtil.d(TAG, "ERROR: DF blocked"); // PSE block and APPLICATION block
                    break;
                default:
                    /**
                     * If the card returns any other value in SW1 SW2, the terminal shall use the
                     * List of AIDs method described in section 12.3.3.
                     */
                    LogUtil.d(TAG, "ERROR: PSE Other error");
                    break;
            }
        } else {
            // no status response
            isNeedTerminate = false;
        }
    }

    public boolean isApplicationBlocked() {
        if (getStatus() != null && getStatus().equals(SW12.ERR_DF_BLOCKED)) {
            return true;
        }

        return false;
    }

    private void printFCIDebugInfo() {
        String hexFCIStr = StringUtil.byte2HexStr(getData());
        Map<String, String> tlvMap = TLVUtil.toTlvMap(hexFCIStr);
        if (tlvMap.containsKey(EMVTag.tag6F)) {
            tag6F = tlvMap.get(EMVTag.tag6F);
            LogUtil.d(TAG, "tag6F=" + tag6F);
        }

        if (tlvMap.containsKey(EMVTag.tag84)) {
            tag84 = tlvMap.get(EMVTag.tag84);
            LogUtil.d(TAG, "tag84=" + tag84);
        }

        if (tlvMap.containsKey(EMVTag.tagA5)) {
            tagA5 = tlvMap.get(EMVTag.tagA5);
            LogUtil.d(TAG, "tagA5=" + tagA5);
        }

        if (tlvMap.containsKey(EMVTag.tag88)) {
            tag88 = tlvMap.get(EMVTag.tag88);
            LogUtil.d(TAG, "tag88=" + tag88);
        }

        if (tlvMap.containsKey(EMVTag.tag5F2D)) {
            tag5F2D = tlvMap.get(EMVTag.tag5F2D);
            LogUtil.d(TAG, "tag5F2D=" + tag5F2D);
        }

        if (tlvMap.containsKey(EMVTag.tag9F11)) {
            tag9F11 = tlvMap.get(EMVTag.tag9F11);
            LogUtil.d(TAG, "tag9F11=" + tag9F11);
        }

        if (tlvMap.containsKey(EMVTag.tagBF0C)) {
            tagBF0C = tlvMap.get(EMVTag.tagBF0C);
            LogUtil.d(TAG, "tagBF0C=" + tagBF0C);
        }

        if (tlvMap.containsKey(EMVTag.tag50)) {
            tag50 = tlvMap.get(EMVTag.tag50);
            LogUtil.d(TAG, "tag50=" + tag50);
        }

        if (tlvMap.containsKey(EMVTag.tag87)) {
            tag87 = tlvMap.get(EMVTag.tag87);
            LogUtil.d(TAG, "tag87=" + tag87);
        }

        if (tlvMap.containsKey(EMVTag.tag9F38)) {
            tag9F38 = tlvMap.get(EMVTag.tag9F38);
            LogUtil.d(TAG, "tag9F38=" + tag9F38);
        }

        if (tlvMap.containsKey(EMVTag.tag9F12)) {
            tag9F12 = tlvMap.get(EMVTag.tag9F12);
            LogUtil.d(TAG, "tag9F12=" + tag9F12);
        }

        if (tlvMap.containsKey(EMVTag.tag9F4D)) {
            tag9F4D = tlvMap.get(EMVTag.tag9F4D);
            LogUtil.d(TAG, "tag9F4D=" + tag9F4D);
        }
    }

    /**
     * Tag Value Presence
     * '6F' FCI Template M
     * '84' DF Name M
     * 'A5' FCI Proprietary Template M
     * '88' SFI of the Directory Elementary File M
     * '5F2D' Language Preference O
     * '9F11' Issuer Code Table Index O
     * 'BF0C' FCI Issuer Discretionary Data O
     * 'XXXX'
     * (Tag
     * constructed
     * according
     * to Book 3,
     * Annex B)
     * 1 or more additional
     * proprietary data
     * elements from an
     * application provider,
     * issuer, or IC card
     * supplier, or
     * EMV-defined tags that
     * are specifically allocated
     * to 'BF0C'
     * O
     * Table 43: SELECT Response Message Data Field (FCI) of the PSE
     */

    /**
     * Tag Value Presence
     * '6F' FCI Template M
     * '84' DF Name M
     * 'A5' FCI Proprietary Template M
     * '88' SFI of the Directory Elementary File M
     * 'BF0C' FCI Issuer Discretionary Data O
     * 'XXXX'
     * (Tag
     * constructed
     * according to
     * Book 3,
     * Annex B)
     * 1 or more additional
     * proprietary data elements
     * from an application
     * provider, issuer, or IC card
     * supplier, or EMV-defined
     * tags that are specifically
     * allocated to 'BF0C'
     * O
     * Table 44: SELECT Response Message Data Field (FCI) of a DDF
     */

    /**
     * Tag Value Presence
     * '6F' FCI Template M
     * '84' DF Name M
     * 'A5' FCI Proprietary Template M
     * '50' Application Label M
     * '87' Application Priority Indicator O
     * '9F38' PDOL O
     * '5F2D' Language Preference O
     * '9F11' Issuer Code Table Index O
     * '9F12' Application Preferred Name O
     * 'BF0C' FCI Issuer Discretionary Data O
     * '9F4D' Log Entry O
     * 'XXXX'
     * (Tag
     * constructed
     * according to
     * Book 3,
     * Annex B)
     * 1 or more additional
     * proprietary data elements
     * from an application
     * provider, issuer, or IC card
     * supplier, or EMV-defined
     * tags that are specifically
     * allocated to 'BF0C'
     * O
     * Table 45: SELECT Response Message Data Field (FCI) of an ADF
     */

    public String getTag6F() {
        return tag6F;
    }

    public void setTag6F(String tag6F) {
        this.tag6F = tag6F;
    }

    public String getTag84() {
        if (tag84 == null) {
            tag84 = "";
        }
        return tag84;
    }

    public void setTag84(String tag84) {
        this.tag84 = tag84;
    }

    public String getTagA5() {
        return tagA5;
    }

    public void setTagA5(String tagA5) {
        this.tagA5 = tagA5;
    }

    public String getTag88() {
        return tag88;
    }

    public void setTag88(String tag88) {
        this.tag88 = tag88;
    }

    public String getTag5F2D() {
        return tag5F2D;
    }

    public void setTag5F2D(String tag5F2D) {
        this.tag5F2D = tag5F2D;
    }

    public String getTag9F11() {
        return tag9F11;
    }

    public void setTag9F11(String tag9F11) {
        this.tag9F11 = tag9F11;
    }

    public String getTagBF0C() {
        return tagBF0C;
    }

    public void setTagBF0C(String tagBF0C) {
        this.tagBF0C = tagBF0C;
    }

    public String getTag50() {
        return tag50;
    }

    public void setTag50(String tag50) {
        this.tag50 = tag50;
    }

    public String getTag87() {
        return tag87;
    }

    public void setTag87(String tag87) {
        this.tag87 = tag87;
    }

    public String getTag9F38() {
        return tag9F38;
    }

    public void setTag9F38(String tag9F38) {
        this.tag9F38 = tag9F38;
    }

    public String getTag9F12() {
        return tag9F12;
    }

    public void setTag9F12(String tag9F12) {
        this.tag9F12 = tag9F12;
    }

    public String getTag9F4D() {
        return tag9F4D;
    }

    public void setTag9F4D(String tag9F4D) {
        this.tag9F4D = tag9F4D;
    }

    public boolean isPSESelect() {
        return isPSESelect;
    }

    public void setPSESelect(boolean PSESelect) {
        isPSESelect = PSESelect;
    }

    public boolean isDDFSelect() {
        return isDDFSelect;
    }

    public void setDDFSelect(boolean DDFSelect) {
        isDDFSelect = DDFSelect;
    }

    public boolean isADFSelect() {
        return isADFSelect;
    }

    public void setADFSelect(boolean ADFSelect) {
        isADFSelect = ADFSelect;
    }

    public boolean isNeedTerminate() {
        return isNeedTerminate;
    }
}
