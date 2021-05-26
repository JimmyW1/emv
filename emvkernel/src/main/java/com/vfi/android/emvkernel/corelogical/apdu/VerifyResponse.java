package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;
import com.vfi.android.emvkernel.data.consts.SW12;

public class VerifyResponse extends ApduResponse {
    private boolean isPinBlocked;
    private boolean zeroRetryRemain;
    public VerifyResponse(byte[] response) {
        super(response);

        if (!isSuccess()) {
            if (getStatus() != null) {
                switch (getStatus()) {
                    case SW12.OPFAIL_INVALID_KEY_ID:
                    case SW12.OPFAIL_INVALID_DATA:
                        isPinBlocked = true;
                        break;
                    case SW12.OPFAIL_NO_RETRY_REMAIN:
                        zeroRetryRemain = true;
                        break;
                }
            }
        }
    }

    public boolean isPinBlocked() {
        return isPinBlocked;
    }

    public void setPinBlocked(boolean pinBlocked) {
        isPinBlocked = pinBlocked;
    }

    public boolean isZeroRetryRemain() {
        return zeroRetryRemain;
    }

    public void setZeroRetryRemain(boolean zeroRetryRemain) {
        this.zeroRetryRemain = zeroRetryRemain;
    }
}
