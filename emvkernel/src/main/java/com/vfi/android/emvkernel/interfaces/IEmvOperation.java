package com.vfi.android.emvkernel.interfaces;

import com.vfi.android.emvkernel.data.beans.EmvParams;

import java.util.List;

public interface IEmvOperation {
    int initEmvFlow(EmvParams emvParams);
    void startEMVFlow(IEMVHandler emvHandler);
    void stopEmvFlow();
    void setEmvTag(String tag, String value);
    String getEmvTag(String tag);
    String getEmvTags(List<String> tagList);
}
