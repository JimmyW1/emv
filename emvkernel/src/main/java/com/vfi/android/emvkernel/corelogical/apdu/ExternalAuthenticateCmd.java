package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.libtools.utils.StringUtil;

public class ExternalAuthenticateCmd extends ApduCmd {

    public ExternalAuthenticateCmd(String issuerAuthenticationData) {
        setCla((byte) 0x00);
        setIns((byte) 0x82);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        setLc((byte) issuerAuthenticationData.length());
        setData(issuerAuthenticationData.getBytes());
    }
}
