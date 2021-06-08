package com.vfi.android.emvkernel.database;

import com.vfi.android.emvkernel.data.beans.TransRecord;

import java.util.List;

public interface IDbOperation {
    void saveEmvAppParamList(int groupId, List<String> emvAppList);
    List<String> getEmvAppParamList(int groupId);
    void saveEmvKeyParamList(int groupId, List<String> emvKeyList);
    List<String> getEmvKeyParamList(int groupId);
    TransRecord getLatestTransRecord(String pan, String panSeqNum, String currentTime, long timeScopeSeconds);
    void saveTransRecords(TransRecord transRecord);
}
