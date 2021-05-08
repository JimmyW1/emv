package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.libtools.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CvmList {
    private String amountX;
    private String amountY;
    private List<CvmRule> cvmRules = new ArrayList<>();

    public CvmList(String hexValue) {
        this.amountX = hexValue.substring(0, 8);
        this.amountY = hexValue.substring(8, 16);
        byte[] cvmRulesBytes = StringUtil.hexStr2Bytes(hexValue.substring(16));
        for (int i = 0; i < cvmRulesBytes.length; i+=2) {
            cvmRules.add(new CvmRule(cvmRulesBytes[i], cvmRulesBytes[i+1]));
        }
    }

    public List<CvmRule> getCvmRules() {
        return cvmRules;
    }

    public String getAmountX() {
        return amountX;
    }

    public String getAmountY() {
        return amountY;
    }
}
