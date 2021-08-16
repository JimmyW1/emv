package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;

public class GenerateApplicationCryptogramCmd extends ApduCmd {
    public static final int TYPE_AAC = 0;
    public static final int TYPE_ARQC = 1;
    public static final int TYPE_TC = 2;

    public GenerateApplicationCryptogramCmd(int type, boolean isRequireCDA, byte[] data) {
        setCla((byte) 0x80);
        setIns((byte) 0xAE);

        byte p1 = 0x00;
        if (type == TYPE_ARQC) {
            p1 = (byte) 0x80;
        } else if (type == TYPE_TC) {
            p1 = 0x40;
        } else {
            p1 = 0x00;
        }

        if (isRequireCDA) {
            p1 |= 0x10;
        }

        setP1(p1);
        setP2((byte) 0x00);

        if (data != null) {
            setLc((byte) data.length);
            setData(data);
        }
        setLe((byte) 0x00);
    }
}
