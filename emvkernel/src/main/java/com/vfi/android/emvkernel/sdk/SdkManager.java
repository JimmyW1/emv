package com.vfi.android.emvkernel.sdk;

import com.vfi.android.emvkernel.database.DbManager;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;

public final class SdkManager {
    private SDKConfig sdkConfig;
    private EmvManager emvManager;
    private DbManager dbManager;

    private SdkManager() {
    }

    private static class SingletonHolder {
        private static final SdkManager INSTANCE = new SdkManager();
    }

    public static final SdkManager getInstance(SDKConfig sdkConfig) {
        SdkManager sdkManager = SingletonHolder.INSTANCE;
        sdkManager.setSdkConfig(sdkConfig);
        sdkManager.setDbManager(DbManager.getInstance(sdkConfig.getDbRootPath()));

        if (sdkConfig.getEmvComm() == null) {
            LogUtil.e(TAGS.SDK, "Need config Emv communication");
            return null;
        }
        sdkManager.setEmvManager(EmvManager.getInstance(sdkConfig.getEmvComm()));

        return sdkManager;
    }

    /**
     * use by sdk internal.
     * @return
     */
    public static final SdkManager getInstance() {
        SdkManager sdkManager = SingletonHolder.INSTANCE;
        if (sdkManager.isInitialized()) {
            return sdkManager;
        } else {
            return null;
        }
    }

    public boolean isInitialized() {
        if (sdkConfig != null) {
            return true;
        }

        return false;
    }

    public EmvManager getEmvManager() {
        return emvManager;
    }

    public DbManager getDbManager() {
        return dbManager;
    }

    /**
     * If terminal have multi payment application, each application may need different configs. such as emv parameters.
     * So use this interface to indicate payment own config path.
     * @param rootPath
     */
    public void setDbRootPath(String rootPath) {
        LogUtil.d(TAGS.DATABASE, "setDbRootPath rootPath=[" + rootPath + "]");

        dbManager = DbManager.getInstance(rootPath);
    }

    public void setDbManager(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setSdkConfig(SDKConfig sdkConfig) {
        this.sdkConfig = sdkConfig;
    }

    public void setEmvManager(EmvManager emvManager) {
        this.emvManager = emvManager;
    }
}
