package com.vfi.android.emvkernel.data.consts;

public class CvmType {
    public static final byte PLAINTEXT_PIN_VERIFICATION_ICC = 0X01;
    public static final byte ENCIPHERED_PIN_VERIFIED_ONLINE = 0X02;
    public static final byte PLAINTEXT_PIN_VERIFICATION_ICC_AND_SIGNATURE = 0X03;
    public static final byte ENCIPHERED_PIN_VERIFICATION_ICC = 0X04;
    public static final byte ENCIPHERED_PIN_VERIFICATION_ICC_AND_SIGNATURE = 0X05;
    public static final byte SIGNATURE = 0X1E;
    public static final byte NO_CVM_REQUIRED = 0X1F;
}
