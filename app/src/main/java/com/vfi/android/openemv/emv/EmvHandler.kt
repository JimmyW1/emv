package com.vfi.android.openemv.emv

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.vfi.android.emvkernel.data.beans.*
import com.vfi.android.emvkernel.interfaces.IEmvHandler
import com.vfi.android.emvkernel.sdk.EmvManager
import com.vfi.android.libtools.consts.TAGS
import com.vfi.android.libtools.utils.LogUtil
import com.vfi.android.openemv.utils.DialogUtil

class EmvHandler : IEmvHandler {
    private var emvManager:EmvManager;
    private val context:Context;

    constructor(context: Context, emvManger: EmvManager) {
        this.emvManager = emvManger;
        this.context = context;
    }

    override fun onRequestOnlineProcess(onlineInfo: OnlineInfo?) {
        TODO("Not yet implemented")
    }

    override fun onSelectApplication(appList: MutableList<AppInfo>?) {
        var builder = AlertDialog.Builder(context)
        builder.setTitle("Select Application")
        var size: Int? = appList?.size?.plus(1)
        var appNameList = size?.let { arrayOfNulls<String>(it) }
        var index:Int = 0;
        if (appList != null) {
            for (app in appList) {
                appNameList?.set(index++, app.aid)
            }

            var cancelledIndex = index;
            appNameList?.set(index++, "Cancel")

            builder.setItems(appNameList) { dialog, which  ->
                var isCancelled  = false;
                if (which == cancelledIndex) {
                    isCancelled = true;
                }

                if (isCancelled) {
                    emvManager.importSelectApplication(isCancelled, AppInfo("", "", '1'.toByte()))
                } else {
                    emvManager.importSelectApplication(isCancelled, appList.get(which))
                }
            }

            var handler = Handler(Looper.getMainLooper());
            handler.post {
                builder.show()
            };
        }
    }

    override fun onConfirmCertInfo(certInfo: CertInfo?) {
        TODO("Not yet implemented")
    }

    override fun onConfirmCardInfo(info: CardInfo?) {
        LogUtil.d(TAGS.EMV_FLOW, "onConfirmCardInfo")

        LogUtil.d(TAGS.EMV_FLOW, "CardInfo=[" + info.toString() + "]")
        emvManager.importCardConfirmResult(true)
    }

    override fun onRequestOfflinePIN(retryTimes: Int) {
        var handler = Handler(Looper.getMainLooper());
        handler.post {
            var CONFIRM = 1;
            var CANCEL = 0;
            Toast.makeText(context, "retryTimes=$retryTimes", Toast.LENGTH_LONG).show();
            DialogUtil.showInputDialog(context, "Please Input Offline Pin:", DialogUtil.InputDialogListener() { inputStr: String, isConfirm: Boolean ->
                if (isConfirm) {
                    emvManager.importPin(CONFIRM, inputStr.toByteArray())
                } else {
                    emvManager.importPin(CANCEL, null)
                }
            });
        };
    }

    override fun onTransactionResult(emvResultInfo: EmvResultInfo?) {
        TODO("Not yet implemented")
    }

    override fun onRequestOnlinePIN() {
        TODO("Not yet implemented")
    }
}