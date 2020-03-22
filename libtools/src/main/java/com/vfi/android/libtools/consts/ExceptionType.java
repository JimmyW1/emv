package com.vfi.android.libtools.consts;

public class ExceptionType {
    public static final int DEVICE_SERVICE_NOT_EXIST = 1;
    public static final int CHECK_CARD_TIMEOUT       = 2;
    public static final int CHECK_CARD_FAILED        = 3;

    public static String toDebugString(int exceptionType) {
        switch (exceptionType) {
            case DEVICE_SERVICE_NOT_EXIST:
                return "DEVICE_SERVICE_NOT_EXIST";
            case  CHECK_CARD_TIMEOUT:
                return "CHECK_CARD_TIMEOUT";
            case CHECK_CARD_FAILED:
                return "CHECK_CARD_FAILED";
        }

        return "" + exceptionType;
    }
}
