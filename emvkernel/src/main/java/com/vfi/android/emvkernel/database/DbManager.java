package com.vfi.android.emvkernel.database;

import java.io.File;

public class DbManager implements IDbOperation {
    private String dbRootPath;

    private static class SingletonHolder {
        private static final DbManager INSTANCE = new DbManager();
    }

    private DbManager() {

    }

    public static DbManager getInstance(String dbRootPath) {
        DbManager dbManager = SingletonHolder.INSTANCE;
        if (dbManager.getDbRootPath() == null) {
            dbManager.setDbRootPath(dbRootPath);
            dbRootPath = "/sdcard/emv_param";
            File file = new File(dbRootPath);
            if (file.exists()) {
                if (!file.isDirectory()) {
                    file.delete();
                    file.mkdir();
                }
            } else {
                file.mkdir();
            }
        } else if (!dbManager.getDbRootPath().equals(dbRootPath)) {
            // change db path
        }

        return SingletonHolder.INSTANCE;
    }

    public String getDbRootPath() {
        return dbRootPath;
    }

    public void setDbRootPath(String dbRootPath) {
        this.dbRootPath = dbRootPath;
    }
}
