package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.libtools.utils.StringUtil;

public class ApplicationBlockCmd extends ApduCmd {
    private boolean isSecureMessage;

    public ApplicationBlockCmd(boolean isSecureMessage) {
        this.isSecureMessage = isSecureMessage;

        if (isSecureMessage) {
            setCla((byte) 0x8C);
        } else {
            setCla((byte) 0x84);
        }

        setIns((byte) 0x1E);
        setP1((byte) 0x00);
        setP2((byte) 0x00);

        byte[] mac = new byte[0];
        setLc((byte) mac.length);
        setData(mac);
    }
}
