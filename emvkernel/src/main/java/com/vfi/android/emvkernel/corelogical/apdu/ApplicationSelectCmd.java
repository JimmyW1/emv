package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;

public class ApplicationSelectCmd extends ApduCmd {
    private String appName;

    public ApplicationSelectCmd(boolean isSelectByName, boolean isSelectFirst, String appName) {
        if (appName == null) {
            appName = "";
        }

        this.appName = appName;

        setCla((byte) 0x00);
        setIns((byte) 0xA4);
        if (isSelectByName) {
            setP1((byte) 0x04);
        } else {
            setP1((byte) 0x00);
        }

        if (isSelectFirst) {
            setP2((byte) 0x00);
        } else {
            setP2((byte) 0x02);
        }

        setLc((byte) appName.length());
        setData(appName.getBytes());
        setLe((byte) 0x00);
    }
}
