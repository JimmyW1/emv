package com.vfi.android.emvkernel.interfaces;

import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.emvkernel.data.beans.ApduResponse;

public interface IComm {
    ApduResponse executeApduCmd(ApduCmd apduCmd);
}
