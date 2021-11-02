package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

public class ScriptCmd extends ApduCmd {
    private byte[] apdu;

    public ScriptCmd(String apduHex) {
        this.apdu = StringUtil.hexStr2Bytes(apduHex);
    }

    @Override
    public byte[] getApduCmd() {
        LogUtil.d(TAG, "=============cmd start==========================");
        LogUtil.d(TAG, "Cmd =[" + StringUtil.byte2HexStr(apdu) + "]");
        LogUtil.d(TAG, "=============cmd end============================");
        return apdu;
    }
}
