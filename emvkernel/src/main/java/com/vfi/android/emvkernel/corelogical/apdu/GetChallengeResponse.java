package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduResponse;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

public class GetChallengeResponse extends ApduResponse {
    private byte[] unpredictableNumber;

    public GetChallengeResponse(byte[] response) {
        super(response);
        if (isSuccess()) {
            unpredictableNumber = getData();
            LogUtil.d(TAG, "unpredictableNumber hex=[" + StringUtil.byte2HexStr(unpredictableNumber) + "]");
            if (unpredictableNumber.length != 8) {
                setSuccess(false);
            }
        }
    }

    public byte[] getUnpredictableNumber() {
        return unpredictableNumber;
    }

    public void setUnpredictableNumber(byte[] unpredictableNumber) {
        this.unpredictableNumber = unpredictableNumber;
    }
}
