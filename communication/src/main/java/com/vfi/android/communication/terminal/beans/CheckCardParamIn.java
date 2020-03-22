package com.vfi.android.communication.terminal.beans;

public class CheckCardParamIn {

    private boolean isSupportMagCard;

    private boolean isSupportICCard;

    private boolean isSupportRFCard;


    public CheckCardParamIn() { }

    private int timeout;

    public boolean isSupportMagCard() {
        return isSupportMagCard;
    }

    public CheckCardParamIn setSupportMagCard(boolean supportMagCard) {
        isSupportMagCard = supportMagCard;
        return this;
    }

    public boolean isSupportICCard() {
        return isSupportICCard;
    }

    public CheckCardParamIn setSupportICCard(boolean supportICCard) {
        isSupportICCard = supportICCard;
        return this;
    }

    public boolean isSupportRFCard() {
        return isSupportRFCard;
    }

    public CheckCardParamIn setSupportRFCard(boolean supportRFCard) {
        isSupportRFCard = supportRFCard;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public CheckCardParamIn setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
}
