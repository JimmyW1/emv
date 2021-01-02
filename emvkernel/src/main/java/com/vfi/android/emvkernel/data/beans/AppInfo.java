package com.vfi.android.emvkernel.data.beans;

public class AppInfo {
    private String appName;
    private String aid;
    private byte appPriorityIndicator;

    public AppInfo(String appName, String aid, byte appPriorityIndicator) {
        this.appName = appName;
        this.aid = aid;
        this.appPriorityIndicator = appPriorityIndicator;
    }

    public String getAid() {
        return aid;
    }

    public String getAppName() {
        return appName;
    }

    public byte getAppPriorityIndicator() {
        return appPriorityIndicator;
    }
}
