package com.vfi.android.openemv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vfi.android.communication.terminal.deviceservice.IPosServiceImpl
import com.vfi.android.communication.terminal.interfaces.IPosService
import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectCmd
import com.vfi.android.libtools.utils.LogUtil
import com.vfi.android.libtools.utils.StringUtil
import com.vfi.android.libtools.utils.TLVUtil

class MainActivity : AppCompatActivity() {
    private var TAG = "TEST";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        TLVUtil.toTlvMap("9F340401010202");
//        TLVUtil.toTlvMap("DF81190101");
        var map = TLVUtil.toTlvMap("6F19840E315041592E5359532E4444463031A5078801019F110101");
        LogUtil.d(TAG, "TAG6F=[" + map.get("6F") + "]");
        LogUtil.d(TAG, "TAG84=[" + map.get("84") + "]");
        LogUtil.d(TAG, "TAGA5=[" + map.get("A5") + "]");
        LogUtil.d(TAG, "TAG88=[" + map.get("88") + "]");
        LogUtil.d(TAG, "TAG5F2D=[" + map.get("5F2D") + "]");
        LogUtil.d(TAG, "TAG9F11=[" + map.get("9F11") + "]");
        LogUtil.d(TAG, "TAGBF0C=[" + map.get("BF0C") + "]");


        var iposService:IPosService = IPosServiceImpl(this)
        iposService.bind().doOnComplete {
            iposService.powerOnSmartCardReader().subscribe();
            var isCardPresent:Boolean = iposService.isIcCardPresent.blockingSingle();
            LogUtil.d(TAG, "isCardPresent=[$isCardPresent]");
            var response = iposService.executeAPDU(ApplicationSelectCmd(true, true, "1PAY.SYS.DDF01").apduCmd).blockingSingle();
            LogUtil.d(TAG, "response=[" + StringUtil.byte2HexStr(response) + "]");
        }.subscribe()
    }
}
