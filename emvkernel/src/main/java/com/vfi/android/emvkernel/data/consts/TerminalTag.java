package com.vfi.android.emvkernel.data.consts;

public class TerminalTag {
    public final static String AID = "9F06";  // AID Name
    public final static String ASI = "DF01";  // ASI - Application Selection Indicator
    public final static String TVR = "95";  // TVR - Terminal Verification Results page145
    public final static String TSI = "9B";  // TSI - Transaction Status Information page148
    public final static String tag9F02 = "9F02";  // Amount, Authorised (Numeric) n 12
    public final static String tag5F2A = "5F2A";  // Transaction Currency Code n 3
    public final static String E_CASH_INDICATOR = "9F7A"; // PBOC 电子现金终端支持指示器（EC Terminal Support Indicator） 9F7A 1 b
    public final static String E_CASH_TRANS_LIMIT = "9F7B"; //PBOC 电子现金终端交易限额（EC Terminal Transaction Limit） 9F7B 6 n 12
}
