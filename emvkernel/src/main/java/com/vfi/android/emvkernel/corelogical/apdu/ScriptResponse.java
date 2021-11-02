package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;
import com.vfi.android.libtools.utils.LogUtil;

public class ScriptResponse extends ApduResponse {
    public ScriptResponse(byte[] response) {
        super(response);
    }

    @Override
    public boolean isSuccess() {
        if (isSuccess()) {
            return true;
        } else {
            /**
             * Book3, Page
             * If during the processing of an issuer script command, as defined in section 10.10,
             * the card returns a warning condition (SW1 SW2 = '62XX' or '63xx'), the terminal
             * shall continue with the next command from the Issuer Script (if any).
             */
            if (getStatus() != null && (getStatus().startsWith("62") || getStatus().startsWith("63"))) {
                LogUtil.d(TAG, "Warning Condition, continue");
                return true;
            }
        }

        return false;
    }
}
