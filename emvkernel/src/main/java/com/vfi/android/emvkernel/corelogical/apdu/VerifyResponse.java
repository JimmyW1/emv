package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;

public class VerifyResponse extends ApduResponse {
    public VerifyResponse(byte[] response) {
        super(response);
    }
}
