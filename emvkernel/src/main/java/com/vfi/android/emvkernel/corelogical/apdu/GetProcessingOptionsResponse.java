package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;

public class GetProcessingOptionsResponse extends ApduResponse {
    public GetProcessingOptionsResponse(byte[] response) {
        super(response);
    }
}
