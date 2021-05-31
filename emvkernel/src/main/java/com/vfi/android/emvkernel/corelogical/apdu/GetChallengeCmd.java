package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;

public class GetChallengeCmd extends ApduCmd {
    public GetChallengeCmd() {
        setCla((byte) 0x00);
        setIns((byte) 0x84);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        // Lc Not present
        // Data Not present
        setLe((byte)0x00);
    }
}
