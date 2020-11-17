package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;

public class ReadRecordCmd extends ApduCmd {

    public ReadRecordCmd(byte sfi, byte recordNum) {
        setCla((byte) 0x00);
        setIns((byte) 0xB2);
        setP1(recordNum);
        setP2((byte) (recordNum << 3 | 0x40));
        setLe((byte) 0x00);
    }
}
