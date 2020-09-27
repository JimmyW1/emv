package com.vfi.android.openemv.emv

import com.vfi.android.emvkernel.data.beans.*
import com.vfi.android.emvkernel.interfaces.IEmvHandler

class EmvHandler : IEmvHandler {
    override fun onRequestOnlineProcess(onlineInfo: OnlineInfo?) {
        TODO("Not yet implemented")
    }

    override fun onSelectApplication(appList: MutableList<AppInfo>?) {
        TODO("Not yet implemented")
    }

    override fun onConfirmCertInfo(certInfo: CertInfo?) {
        TODO("Not yet implemented")
    }

    override fun onConfirmCardInfo(info: CardInfo?) {
        TODO("Not yet implemented")
    }

    override fun onRequestOfflinePIN(retryTimes: Int) {
        TODO("Not yet implemented")
    }

    override fun onTransactionResult(emvResultInfo: EmvResultInfo?) {
        TODO("Not yet implemented")
    }

    override fun onRequestOnlinePIN() {
        TODO("Not yet implemented")
    }
}