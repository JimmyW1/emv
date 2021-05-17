package com.vfi.android.emvkernel.corelogical.apdu;

import com.vfi.android.emvkernel.data.beans.ApduCmd;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

public class VerifyCmd extends ApduCmd {
    public VerifyCmd(boolean isEnciphered, byte[] pin) {
        if (pin == null) {
            pin = new byte[0];
        }

        setCla((byte) 0x00);
        setIns((byte) 0x20);
        setP1((byte) 0x00);
        /**
         * 0 0 0 0 0 0 0 As defined in ISO/IEC 7816-4 3  // The value of P2 = ‘00’ is not used by this specification.
         * 1 0 0 0 0 0 0 0 Plaintext PIN, format as defined below
         * 1 0 0 0 0 x x x RFU for this specification
         * 1 0 0 0 1 0 0 0 Enciphered PIN, format as defined in Book 2
         * 1 0 0 0 1 0 x x RFU for this specification
         * 1 0 0 0 1 1 x x RFU for the individual payment systems
         * 1 0 0 1 x x x x RFU for the issuer
         */
        if (isEnciphered) {
            setP2((byte)0x88);
        } else {
            setP2((byte) 0x80);
        }

        setLc((byte) pin.length);
        setData(pin);
    }
}
