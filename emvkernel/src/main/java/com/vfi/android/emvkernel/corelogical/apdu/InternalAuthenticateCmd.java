package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.libtools.utils.StringUtil;

public class InternalAuthenticateCmd extends ApduCmd {
    public InternalAuthenticateCmd(String ddolTagsData) {
        setCla((byte) 0x00);
        setIns((byte) 0x88);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        byte[] dataBytes = StringUtil.hexStr2Bytes(ddolTagsData);
        setLc((byte) dataBytes.length);
        setData(dataBytes);
        setLe((byte) 0x00);
    }
}
