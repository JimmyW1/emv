package com.vfi.android.emvkernel.data.beans;

public class DOLBean {
    private String tag;
    private int formatType;
    private int len;

    public DOLBean(String tag, int len, int formatType) {
        this.tag = tag;
        this.len = len;
        this.formatType = formatType;
    }

    public int getLen() {
        return len;
    }

    public String getTag() {
        return tag;
    }

    public String formatValue(String value) {
        return null;
    }
}
