package com.vfi.android.emvkernel.corelogical.msgs.base;

public class Message {
    private String messageType;

    public Message(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }
}
