package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;

public class ExternalAuthenticateResponse extends ApduResponse {
    public ExternalAuthenticateResponse(byte[] response) {
        super(response);
    }
}
