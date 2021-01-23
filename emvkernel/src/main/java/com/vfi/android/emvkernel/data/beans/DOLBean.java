package com.vfi.android.emvkernel.data.beans;

import com.vfi.android.emvkernel.data.consts.TagFormat;

import java.util.Arrays;

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
        switch (formatType) {
            case TagFormat.FM_N:
                return padding(value, len * 2, true, (byte) '0');
            case TagFormat.FM_CN:
                return padding(value, len * 2, false, (byte) 'F');
            default:
                return padding(value, len * 2, false, (byte) '0');
        }
    }

    private String padding(String value, int length, boolean isLeftPadding, byte paddingChar) {
        if (value == null) {
            value = "";
        }

        if (value.length() == length) {
            return value;
        } else if (value.length() > length) {
            if (isLeftPadding) {
                return value.substring(value.length() - length, value.length());
            } else {
                return value.substring(0, length);
            }
        } else {
            int paddingLen = length - value.length();
            byte[] chars = new byte[paddingLen];
            Arrays.fill(chars, paddingChar);
            String paddingChars = new String(chars);

            if (isLeftPadding) {
                return paddingChars + value;
            } else {
                return value + paddingChars;
            }
        }
    }
}
