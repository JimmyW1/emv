package com.vfi.android.emvkernel.interfaces;

import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.emvkernel.data.beans.ApduResponse;

public interface IEmvComm {
    boolean powerOnCardReader();
    boolean isCardPresent();
    byte[] executeApduCmd(ApduCmd apduCmd);
    void  powerOffCardReader();
}
