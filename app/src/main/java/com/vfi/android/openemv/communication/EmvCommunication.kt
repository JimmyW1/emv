package com.vfi.android.openemv.communication

import android.content.Context
import com.vfi.android.communication.terminal.deviceservice.IPosServiceImpl
import com.vfi.android.communication.terminal.interfaces.IPosService
import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectResponse
import com.vfi.android.emvkernel.data.beans.ApduCmd
import com.vfi.android.emvkernel.data.beans.ApduResponse
import com.vfi.android.emvkernel.interfaces.IEmvComm
import com.vfi.android.libtools.consts.TAGS
import com.vfi.android.libtools.utils.LogUtil
import com.vfi.android.libtools.utils.StringUtil

class EmvCommunication : IEmvComm {
    private var context: Context;
    private var iposService: IPosService
    final var TAG = TAGS.COMM

    constructor(context: Context) {
        this.context = context;
        this.iposService = IPosServiceImpl(context)

        iposService.bind().doOnComplete {
            LogUtil.d(TAG, "bind finished")
        }.subscribe()
    }

    override fun powerOnCardReader(): Boolean {
        return iposService.powerOnSmartCardReader().blockingSingle();
    }

    override fun isCardPresent(): Boolean {
        var isCardPresent:Boolean = iposService.isIcCardPresent.blockingSingle();
        LogUtil.d(TAG, "isCardPresent=[$isCardPresent]");
        return isCardPresent;
    }

    override fun powerOffCardReader() {
        iposService.powerOffSmartCardReader().blockingSingle();
    }

    override fun executeApduCmd(apduCmd: ApduCmd?): ApduResponse {
        var response = iposService.executeAPDU(apduCmd!!.apduCmd).blockingSingle();
        LogUtil.d(TAG, "response=[" + StringUtil.byte2HexStr(response) + "]");

        var responseApdu = ApplicationSelectResponse(response);
        LogUtil.d(TAG, "isSuccess" + responseApdu.isSuccess);
        return responseApdu;
    }
}