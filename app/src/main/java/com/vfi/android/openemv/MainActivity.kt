package com.vfi.android.openemv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vfi.android.communication.terminal.deviceservice.IPosServiceImpl
import com.vfi.android.communication.terminal.interfaces.IPosService
import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectCmd
import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectResponse
import com.vfi.android.emvkernel.data.beans.EmvParams
import com.vfi.android.emvkernel.interfaces.IEmvHandler
import com.vfi.android.emvkernel.sdk.SDKConfig
import com.vfi.android.emvkernel.sdk.SdkManager
import com.vfi.android.libtools.utils.LogUtil
import com.vfi.android.libtools.utils.StringUtil
import com.vfi.android.libtools.utils.TLVUtil
import com.vfi.android.openemv.communication.EmvCommunication
import com.vfi.android.openemv.emv.EmvHandler
import com.vfi.smartpos.deviceservice.aidl.EMVHandler

class MainActivity : AppCompatActivity() {
    private var TAG = "TEST";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TLVUtil.toTlvMap("B119459A72D408580706FF0103A4A8025735C6E20051000000800020092900000000000137076407643C000000000000000080A0000000250108010001440302");
//        TLVUtil.toTlvMap("DF81190101");
        var map = TLVUtil.toTlvMap("6F19840E315041592E5359532E4444463031A5078801019F110101");
        LogUtil.d(TAG, "TAG6F=[" + map.get("6F") + "]");
        LogUtil.d(TAG, "TAG84=[" + map.get("84") + "]");
        LogUtil.d(TAG, "TAGA5=[" + map.get("A5") + "]");
        LogUtil.d(TAG, "TAG88=[" + map.get("88") + "]");
        LogUtil.d(TAG, "TAG5F2D=[" + map.get("5F2D") + "]");
        LogUtil.d(TAG, "TAG9F11=[" + map.get("9F11") + "]");
        LogUtil.d(TAG, "TAGBF0C=[" + map.get("BF0C") + "]");

        var newMap:HashMap<String, String> = HashMap();
        newMap.put("9F34", "01010102");
        newMap.put("DF81", "01");
        newMap.put("DF81", "010203040506070809100102030405060708091001020304050607080910010203040506070809100102030405060708091001020304050607080910010203040506070809100102030405060708091001020304050607080910010203040506070809100102030405060708091001020304050607080910010203040506070809100102030405060708091001020304050607080910010203040506070809100102030405060708091001020304050607080910");
        var tlvStr = TLVUtil.toTlvStr(newMap);
        LogUtil.d(TAG, "Final Str=[" + tlvStr + "]");

        var sdkConfig = SDKConfig()
        sdkConfig.dbRootPath = "/sdcard/emv_param"
        sdkConfig.emvComm = EmvCommunication(this)

        var sdkManager = SdkManager.getInstance(sdkConfig)
        var emvManager = sdkManager.emvManager

        var emvParams = EmvParams()
        emvParams.isContact = true;
        emvManager.initEmvFlow(emvParams)
        var emvHandler = EmvHandler()
        emvManager.startEMVFlow(emvHandler)
        emvManager.initEmvFlow(emvParams)
        emvManager.startEMVFlow(emvHandler)

//        var iposService:IPosService = IPosServiceImpl(this)
//        iposService.bind().doOnComplete {
//            iposService.powerOnSmartCardReader().subscribe();
//            var isCardPresent:Boolean = iposService.isIcCardPresent.blockingSingle();
//            LogUtil.d(TAG, "isCardPresent=[$isCardPresent]");
//            var response = iposService.executeAPDU(ApplicationSelectCmd(true, true, "1PAY.SYS.DDF01").apduCmd).blockingSingle();
//            LogUtil.d(TAG, "response=[" + StringUtil.byte2HexStr(response) + "]");
//            var responseApdu = ApplicationSelectResponse(response);
//            LogUtil.d(TAG, "isSuccess" + responseApdu.isSuccess);
//            LogUtil.d(TAG, "isTerminate" + responseApdu.isNeedTerminate);
//            LogUtil.d(TAG, "tag84" + responseApdu.tag84);
//        }.subscribe()
    }
}
