package com.vfi.android.emvkernel.data.consts;

public class EMVResultCode {
    public static final int ERR_CARD_BLOCKED = 1001;
    public static final int ERR_APPLICATION_BLOCKED = 1002;
    public static final int ERR_NOT_SUPPORT_CARDHOLDER_SELECT = 1003;
    public static final int ERR_EMPTY_CANDIDATE_LIST = 1004;
    public static final int ERR_CARDHOLDER_CANCELLED_TRANS = 1005;
    public static final int ERR_NOT_SET_EMV_HANDLER = 1006;
    public static final int ERR_MISSING_AIP_AFL = 1007;
    public static final int ERR_GPO_FAILED = 1008;
    public static final int ERR_READ_RECORD_FAILED = 1009;
    public static final int ERR_MISSING_MANDATORY_DATA = 1010;
    public static final int ERR_CARD_HOLDER_CANCELLED = 1011;
    // SDA
    public static final int ERR_MISSING_CERT_AUTH_PUBLIC_KEY = 1012;
    public static final int ERR_ISSUER_PUB_KEY_LEN_DIFFERENT_FROM_AUTHORITY_PUB_KEY = 1013;
    public static final int ERR_ISSUER_PUB_KEY_RECOVERED_FAILED = 1014;
    public static final int ERR_ISSUER_PUB_KEY_RECOVERED_DATA_TRAILER_NOT_BC = 1015;
    public static final int ERR_ISSUER_PUB_KEY_RECOVERED_DATA_HEADER_NOT_6A = 1016;
    public static final int ERR_ISSUER_PUB_KEY_RECOVERED_DATA_CERTIFICATE_FORMAT_NOT_02 = 1017;
    public static final int ERR_ISSUER_PUB_KEY_RECOVERED_DATA_CERTIFICATE_HASH_WRONG = 1018;
    public static final int ERR_HASH_INDICATOR_ALGO_NOT_SUPPORT = 1019;
    public static final int ERR_VERIFY_ISSUER_IDENTIFIER_FAILED = 1020;
    public static final int ERR_ISSUER_PUB_KEY_EXPIRATION = 1021;
    public static final int ERR_PUBLIC_KEY_ALG_INDICATOR_IS_NOT_RECOGNISED = 1022;
    public static final int ERR_SIGNED_STATIC_APP_DATA_HAVE_DIFFERENT_LENGTH_WITH_PUBLIC_KEY = 1023;
    public static final int ERR_SIGNED_STATIC_APP_DATA_TRAILER_NOT_BC = 1024;
    public static final int ERR_SIGNED_STATIC_APP_DATA_HEADER_NOT_6A = 1025;
    public static final int ERR_SIGNED_STATIC_APP_DATA_CERTIFICATE_FORMAT_NOT_03 = 1026;
    public static final int ERR_SIGNED_STATIC_APP_DATA_RECOVERED_FAILED = 1027;
    public static final int ERR_READ_OFFLINE_STATIC_DATA_RECORD_NOT_CODED_WITH_TAG70 = 1028;
    public static final int ERR_OPTIONAL_STATIC_DATA_AUTHENTICATION_TAG_LIST_NOT_ONLY_TAG82 = 1029;
    public static final int ERR_CALCULATE_HASH_IS_NOT_THE_SAME_WITH_RECOVERED_HASH_RESULT = 1030;
    // DDA
    public static final int ERR_MISSING_DDOL = 1031;
    public static final int ERR_MISSING_UNPREDICTABLE_NUMBER = 1032;
    public static final int ERR_EXECUTE_INTERNAL_AUTHENTICATE_FAILED = 1033;
    public static final int ERR_ISSUER_PUB_KEY_LEN_DIFFERENT_FROM_ICC_PUBLIC_KEY_CERT = 1034;
    public static final int ERR_ICC_PUB_KEY_RECOVERED_FAILED = 1035;
    public static final int ERR_ICC_PUB_KEY_RECOVERED_DATA_TRAILER_NOT_BC = 1036;
    public static final int ERR_ICC_PUB_KEY_RECOVERED_DATA_HEADER_NOT_6A = 1037;
    public static final int ERR_ICC_PUB_KEY_RECOVERED_DATA_CERTIFICATE_FORMAT_NOT_04 = 1038;
    public static final int ERR_ICC_PUB_KEY_RECOVERED_DATA_CERTIFICATE_HASH_WRONG = 1039;
    public static final int ERR_VERIFY_APPLICATION_PAN_FAILED = 1040;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_LEN_DIFFERENT_FROM_ICC_PUBLIC_KEY = 1041;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_RECOVERED_FAILED = 1042;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_RECOVERED_DATA_TRAILER_NOT_BC = 1043;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_RECOVERED_DATA_HEADER_NOT_6A = 1044;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_FORMAT_NOT_05 = 1045;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_RECOVERED_DATA_HASH_WRONG = 1046;
    // CVM
    public static final int ERR_PIN_BLOCKED = 1047;
    public static final int ERR_PIN_TRY_LIMIT_EXCEEDED = 1048;
    public static final int ERR_ISSUER_PUB_KEY_LEN_DIFFERENT_FROM_ICC_PIN_PUBLIC_KEY_CERT = 1049;
    public static final int ERR_ICC_PIN_PUB_KEY_RECOVERED_FAILED = 1050;
    public static final int ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_TRAILER_NOT_BC = 1051;
    public static final int ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_HEADER_NOT_6A = 1052;
    public static final int ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_CERTIFICATE_FORMAT_NOT_04 = 1053;
    public static final int ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_CERTIFICATE_HASH_WRONG = 1054;

}
