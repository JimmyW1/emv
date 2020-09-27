package com.vfi.android.emvkernel.sdk;

public final class SdkManager {
    private EmvManager emvManager;

    private SdkManager(){ }

    private static class SingletonHolder {
        private static final SdkManager INSTANCE = new SdkManager();
    }

    public static final SdkManager getInstance() {
        return SdkManager.SingletonHolder.INSTANCE;
    }

    public EmvManager getEmvManager() {
        return emvManager;
    }
}
