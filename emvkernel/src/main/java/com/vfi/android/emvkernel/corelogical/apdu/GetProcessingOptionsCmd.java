package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.libtools.utils.StringUtil;

public class GetProcessingOptionsCmd extends ApduCmd {

    public GetProcessingOptionsCmd(String pdolData) {
        setCla((byte) 0x80);
        setIns((byte) 0xA8);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        if (pdolData == null || pdolData.length() == 0) {
            setLc((byte) 0x02);
            setData(StringUtil.hexStr2Bytes("8300")); // page 91 book 3 - if PDOL not exist use command data "8300" to indicate the length of value in the command data is zero.
        } else {
            String data = "83" + String.format("%02X", pdolData.length() / 2) + pdolData;
            byte[] dataBytes = StringUtil.hexStr2Bytes(data);
            setLc((byte) dataBytes.length);
            setData(dataBytes);
        }
        setLe((byte) 0x00);
    }
}
