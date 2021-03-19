package com.vfi.android.emvkernel.corelogical.states.common;

import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartEmv;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartSelectApp;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.data.beans.EmvTransData;
import com.vfi.android.emvkernel.database.IDbOperation;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.TLVUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.vfi.android.emvkernel.corelogical.states.base.EmvStateType.*;

public class IdleState extends AbstractEmvState {
    public IdleState() {
        super(STATE_IDLE);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        LogUtil.d(TAG, "IdleState msgType=" + context.getMessage().getMessageType());
        if (context.getMessage() instanceof Msg_StartEmv) {
            context.getEmvComm().powerOnCardReader();
            EmvTransData emvTransData = context.getCurrentTransData();
            IDbOperation dbOperation = context.getDbOperation();

            // 1. reset Emv transaction cache
            emvTransData.resetEmvTransData();

            // 2. load terminal application parameters/ CA public keys
            emvTransData.setTerminalApplicationMapList(getEmvAppParamListMap());
            emvTransData.setCaPublicKeyList(getEmvKeyListMap());

            jumpToState(STATE_SELECT_APP);
            sendMessage(new Msg_StartSelectApp());
        }
    }

    private List<Map<String, String>> getEmvAppParamListMap() {
        IDbOperation dbOperation = getEmvContext().getDbOperation();
        List<String> emvAppParamList = dbOperation.getEmvAppParamList(getEmvContext().getEmvParams().getEmvParameterGroup());
        List<Map<String, String>> emvAppParamListMap = new ArrayList<>();
        Map<String, String> map;
        for (String emvAppParam : emvAppParamList) {
            map = TLVUtil.toTlvMap(emvAppParam);
            emvAppParamListMap.add(map);
        }

        return emvAppParamListMap;
    }

    private List<Map<String, String>> getEmvKeyListMap() {
        IDbOperation dbOperation = getEmvContext().getDbOperation();
        List<String> emvCapksList = dbOperation.getEmvKeyParamList(getEmvContext().getEmvParams().getEmvParameterGroup());
        List<Map<String, String>> emvCapksListMap = new ArrayList<>();
        Map<String, String> map;
        for (String emvKeyParam : emvCapksList) {
            map = TLVUtil.toTlvMap(emvKeyParam);
            emvCapksListMap.add(map);
        }

        return emvCapksListMap;
    }
}
