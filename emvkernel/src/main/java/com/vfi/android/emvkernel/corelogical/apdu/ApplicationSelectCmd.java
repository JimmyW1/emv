package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.libtools.utils.StringUtil;

public class ApplicationSelectCmd extends ApduCmd {
    private String appName;
    private boolean isSelectByName = true;

    public ApplicationSelectCmd(boolean isSelectFirst, String appName) {
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

        if (appName.equals("1PAY.SYS.DDF01")) {
            setLc((byte) appName.length());
            setData(appName.getBytes());
        } else {
            byte[] appNameBytes = StringUtil.hexStr2Bytes(appName);
            setLc((byte) appNameBytes.length);
            setData(appNameBytes);
        }
        setLe((byte) 0x00);
    }
}
