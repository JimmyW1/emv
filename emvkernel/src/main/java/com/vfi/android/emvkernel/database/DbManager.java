package com.vfi.android.emvkernel.database;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbManager implements IDbOperation {
    private String dbRootPath;
    private Map<String, List<String>> emvAppParamsMap;
    private Map<String, List<String>> emvKeyParamsMap;

    private static class SingletonHolder {
        private static final DbManager INSTANCE = new DbManager();
    }

    private DbManager() {
        emvAppParamsMap = new HashMap<>();
        emvKeyParamsMap = new HashMap<>();
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

    @Override
    public void saveEmvAppParamList(int groupId, List<String> emvAppList) {

    }

    @Override
    public List<String> getEmvAppParamList(int groupId) {
        List<String> appParamList = new ArrayList<>();
        appParamList.add("9F0608A000000003101001" + "DF010100");
        appParamList.add("9F0605A000000003" + "DF010100");
        appParamList.add("9F0605A000000004" + "DF010100");
        appParamList.add("9F0605A000000333" + "DF010100");
        appParamList.add("9F0605A000000677" + "DF010100");

        return appParamList;
    }

    @Override
    public void saveEmvKeyParamList(int groupId, List<String> emvKeyList) {

    }

    @Override
    public List<String> getEmvKeyParamList(int groupId) {
        return null;
    }
}
