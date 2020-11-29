package com.vfi.android.emvkernel.database;

import java.util.List;

public interface IDbOperation {
    void saveEmvAppParamList(int groupId, List<String> emvAppList);
    List<String> getEmvAppParamList(int groupId);
    void saveEmvKeyParamList(int groupId, List<String> emvKeyList);
    List<String> getEmvKeyParamList(int groupId);
}
