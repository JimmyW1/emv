package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.libtools.utils.StringUtil;

public class GetDataCmd extends ApduCmd {
    /**
     * ATC (tag '9F36')
     * Last Online ATC Register (tag '9F13')
     * PIN Try Counter (tag '9F17')
     * Log Format (tag '9F4F')
     */
    private String type;
    public static final String TYPE_ATC = EMVTag.tag9F36;
    public static final String TYPE_LAST_ONLINE_ATC_REGISTER = EMVTag.tag9F13;
    public static final String TYPE_PIN_TRY_COUNTER = EMVTag.tag9F17;
    public static final String TYPE_LOG_FORMAT = EMVTag.tag9F4F;

    public GetDataCmd(String type) {
        this.type = type;

        setCla((byte) 0x80);
        setIns((byte) 0xCA);

        type = StringUtil.getNonNullStringRightPadding(type, 4);
        byte[] typeBytes = StringUtil.hexStr2Bytes(type);
        setP1(typeBytes[0]);
        setP2(typeBytes[1]);
        // Lc Not present
        // Data Not present
        setLe((byte)0x00);
    }
}
