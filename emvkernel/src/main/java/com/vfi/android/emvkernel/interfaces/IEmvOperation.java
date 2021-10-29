package com.vfi.android.emvkernel.interfaces;

import com.vfi.android.emvkernel.data.beans.AppInfo;
import com.vfi.android.emvkernel.data.beans.EmvParams;
import com.vfi.android.emvkernel.data.beans.OnlineResult;

import java.util.List;

public interface IEmvOperation {
    int initEmvFlow(EmvParams emvParams);
    void startEMVFlow(IEmvHandler emvHandler);
    void stopEmvFlow();
    void importSelectApplication(boolean isCancelled, AppInfo appInfo);
    void importCardConfirmResult(boolean pass);
    void importPin(int option, byte[] pin);
    void inputOnlineResult(OnlineResult result);
    void setEmvTag(String tag, String value);
    String getEmvTag(String tag);
    String getEmvTags(List<String> tagList);
    void saveEmvAppParamList(int groupId, List<String> emvAppList);
    List<String> getEmvAppParamList(int groupId);
    void clearAllEmvAppParams();
    void saveEmvKeyParamList(int groupId, List<String> emvKeyList);
    List<String> getEmvKeyParamList(int groupId);
    void clearAllEmvKeyParams();
}
