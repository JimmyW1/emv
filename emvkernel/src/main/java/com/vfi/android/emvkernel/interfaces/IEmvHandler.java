package com.vfi.android.emvkernel.interfaces;

import com.vfi.android.emvkernel.data.beans.AppInfo;
import com.vfi.android.emvkernel.data.beans.CardInfo;
import com.vfi.android.emvkernel.data.beans.CertInfo;
import com.vfi.android.emvkernel.data.beans.EmvResultInfo;
import com.vfi.android.emvkernel.data.beans.OnlineInfo;

import java.util.List;

public interface IEmvHandler {
    void onSelectApplication(List<AppInfo> appList);
    void onConfirmCardInfo(CardInfo info);
    void onRequestOnlinePIN();
    void onRequestOfflinePIN(int retryTimes);
    void onConfirmCertInfo(CertInfo certInfo);
    void onRequestOnlineProcess(OnlineInfo onlineInfo);
    void onTransactionResult(EmvResultInfo emvResultInfo);
}
