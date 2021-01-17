package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;

public class ApplicationBlockResponse extends ApduResponse {
    public ApplicationBlockResponse(byte[] response) {
        super(response);
    }
}
