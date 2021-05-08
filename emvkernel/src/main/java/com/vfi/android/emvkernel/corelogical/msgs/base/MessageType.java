package com.vfi.android.emvkernel.corelogical.msgs.base;

public class MessageType {
    public static final String MSG_START_EMV = "MSG_START_EMV";
    public static final String MSG_START_SELECT_APP = "MSG_START_SELECT_APP";
    public static final String MSG_STOP_EMV = "MSG_STOP_EMV";
    public static final String MSG_START_GPO = "MSG_START_GPO";
    public static final String MSG_RESELECT_APP_FROM_CANDIDATE_LIST = "MSG_RESELECT_APP_FROM_CANDIDATE_LIST";
    public static final String MSG_START_CARD_CONFIRM = "MSG_START_CARD_CONFIRM";
    public static final String MSG_START_OFFLINE_DATA_AUTH = "MSG_START_OFFLINE_DATA_AUTH";
    public static final String MSG_START_PROCESSING_RESTRICTIONS = "MSG_START_PROCESSING_RESTRICTIONS";
    public static final String MSG_START_CARDHOLDER_VERIFICATION = "MSG_START_CARDHOLDER_VERIFICATION";
    public static final String MSG_START_TERMINAL_RISK_MANAGEMENT = "MSG_START_TERMINAL_RISK_MANAGEMENT";

    // app messages
    public static final String MSG_START_CARDHOLDER_SELECT = "MSG_START_CARDHOLDER_SELECT";
    public static final String MSG_CARDHOLDER_SELECT_FINISHED = "MSG_CARDHOLDER_SELECT_FINISHED";
    public static final String MSG_CARDHOLDER_CONFIRM = "MSG_CARDHOLDER_CONFIRM";
}
