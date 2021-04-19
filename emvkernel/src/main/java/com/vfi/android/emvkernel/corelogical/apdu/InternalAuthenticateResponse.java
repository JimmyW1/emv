package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;

public class InternalAuthenticateResponse extends ApduResponse {
    public InternalAuthenticateResponse(byte[] response) {
        super(response);
    }
}
