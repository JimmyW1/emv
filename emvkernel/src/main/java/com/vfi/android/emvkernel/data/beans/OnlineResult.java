package com.vfi.android.emvkernel.data.beans;

public class OnlineResult {
    private boolean isUnableToGoOnline;
    private String respCode;
    private String authCode;
    private String respField55;

    public OnlineResult() {
    }

    public boolean isUnableToGoOnline() {
        return isUnableToGoOnline;
    }

    public void setUnableToGoOnline(boolean unableToGoOnline) {
        isUnableToGoOnline = unableToGoOnline;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getRespField55() {
        return respField55;
    }

    public void setRespField55(String respField55) {
        this.respField55 = respField55;
    }
}
