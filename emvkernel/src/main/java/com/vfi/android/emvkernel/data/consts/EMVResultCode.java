package com.vfi.android.emvkernel.data.consts;

public class EMVResultCode {
    public static final int SUCCESS = 0;

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
    public static final int ERR_MISSING_CERT_AUTH_PUBLIC_KEY = 1101;
    public static final int ERR_ISSUER_PUB_KEY_LEN_DIFFERENT_FROM_AUTHORITY_PUB_KEY = 1102;
    public static final int ERR_ISSUER_PUB_KEY_RECOVERED_FAILED = 1103;
    public static final int ERR_ISSUER_PUB_KEY_RECOVERED_DATA_TRAILER_NOT_BC = 1104;
    public static final int ERR_ISSUER_PUB_KEY_RECOVERED_DATA_HEADER_NOT_6A = 1105;
    public static final int ERR_ISSUER_PUB_KEY_RECOVERED_DATA_CERTIFICATE_FORMAT_NOT_02 = 1106;
    public static final int ERR_ISSUER_PUB_KEY_RECOVERED_DATA_CERTIFICATE_HASH_WRONG = 1107;
    public static final int ERR_HASH_INDICATOR_ALGO_NOT_SUPPORT = 1108;
    public static final int ERR_VERIFY_ISSUER_IDENTIFIER_FAILED = 1109;
    public static final int ERR_ISSUER_PUB_KEY_EXPIRATION = 1110;
    public static final int ERR_PUBLIC_KEY_ALG_INDICATOR_IS_NOT_RECOGNISED = 1111;
    public static final int ERR_SIGNED_STATIC_APP_DATA_HAVE_DIFFERENT_LENGTH_WITH_PUBLIC_KEY = 1112;
    public static final int ERR_SIGNED_STATIC_APP_DATA_TRAILER_NOT_BC = 1113;
    public static final int ERR_SIGNED_STATIC_APP_DATA_HEADER_NOT_6A = 1114;
    public static final int ERR_SIGNED_STATIC_APP_DATA_CERTIFICATE_FORMAT_NOT_03 = 1115;
    public static final int ERR_SIGNED_STATIC_APP_DATA_RECOVERED_FAILED = 1116;
    public static final int ERR_READ_OFFLINE_STATIC_DATA_RECORD_NOT_CODED_WITH_TAG70 = 1117;
    public static final int ERR_OPTIONAL_STATIC_DATA_AUTHENTICATION_TAG_LIST_NOT_ONLY_TAG82 = 1118;
    public static final int ERR_CALCULATE_HASH_IS_NOT_THE_SAME_WITH_RECOVERED_HASH_RESULT = 1119;
    // DDA
    public static final int ERR_MISSING_DDOL = 1201;
    public static final int ERR_MISSING_UNPREDICTABLE_NUMBER = 1202;
    public static final int ERR_EXECUTE_INTERNAL_AUTHENTICATE_FAILED = 1203;
    public static final int ERR_ISSUER_PUB_KEY_LEN_DIFFERENT_FROM_ICC_PUBLIC_KEY_CERT = 1204;
    public static final int ERR_ICC_PUB_KEY_RECOVERED_FAILED = 1205;
    public static final int ERR_ICC_PUB_KEY_RECOVERED_DATA_TRAILER_NOT_BC = 1206;
    public static final int ERR_ICC_PUB_KEY_RECOVERED_DATA_HEADER_NOT_6A = 1207;
    public static final int ERR_ICC_PUB_KEY_RECOVERED_DATA_CERTIFICATE_FORMAT_NOT_04 = 1208;
    public static final int ERR_ICC_PUB_KEY_RECOVERED_DATA_CERTIFICATE_HASH_WRONG = 1209;
    public static final int ERR_VERIFY_APPLICATION_PAN_FAILED = 1210;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_LEN_DIFFERENT_FROM_ICC_PUBLIC_KEY = 1211;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_RECOVERED_FAILED = 1212;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_RECOVERED_DATA_TRAILER_NOT_BC = 1213;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_RECOVERED_DATA_HEADER_NOT_6A = 1214;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_FORMAT_NOT_05 = 1215;
    public static final int ERR_SIGNED_DYNAMIC_APPLICATION_DATA_RECOVERED_DATA_HASH_WRONG = 1216;
    // CVM
    public static final int ERR_PIN_BLOCKED = 1301;
    public static final int ERR_PIN_TRY_LIMIT_EXCEEDED = 1302;
    public static final int ERR_ISSUER_PUB_KEY_LEN_DIFFERENT_FROM_ICC_PIN_PUBLIC_KEY_CERT = 1303;
    public static final int ERR_ICC_PIN_PUB_KEY_RECOVERED_FAILED = 1304;
    public static final int ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_TRAILER_NOT_BC = 1305;
    public static final int ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_HEADER_NOT_6A = 1306;
    public static final int ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_CERTIFICATE_FORMAT_NOT_04 = 1307;
    public static final int ERR_ICC_PIN_PUB_KEY_RECOVERED_DATA_CERTIFICATE_HASH_WRONG = 1308;
    // TAA
    public static final int ERR_TAA_RESULT_AAC = 1401;


}
