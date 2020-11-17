package com.vfi.android.openemv.communication

import com.vfi.android.communication.terminal.interfaces.IPosService
import com.vfi.android.emvkernel.data.beans.ApduCmd
import com.vfi.android.emvkernel.interfaces.IEmvComm
import com.vfi.android.libtools.consts.TAGS
import com.vfi.android.libtools.utils.LogUtil
import com.vfi.android.libtools.utils.StringUtil

class EmvCommunication : IEmvComm {
    private var iposService: IPosService
    final var TAG = TAGS.COMM

    constructor(iPosService: IPosService) {
        this.iposService = iPosService;
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

    override fun executeApduCmd(apduCmd: ApduCmd?): ByteArray {
        var response = iposService.executeAPDU(apduCmd!!.apduCmd).blockingSingle();
        LogUtil.d(TAG, "response=[" + StringUtil.byte2HexStr(response) + "]");

        return response;
    }
}