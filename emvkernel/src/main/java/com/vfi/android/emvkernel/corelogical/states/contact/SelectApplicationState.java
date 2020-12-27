package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectCmd;
import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectResponse;
import com.vfi.android.emvkernel.corelogical.apdu.ReadRecordCmd;
import com.vfi.android.emvkernel.corelogical.apdu.ReadRecordResponse;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartSelectApp;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StopEmv;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import static com.vfi.android.emvkernel.corelogical.states.base.EmvStateType.*;

public class SelectApplicationState extends AbstractEmvState {
    public SelectApplicationState() {
        super(STATE_SELECT_APP);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);
        LogUtil.d(TAG, "SelectApplicationState msgType=" + context.getMessage().getMessageType());
        if (context.getMessage() instanceof Msg_StartSelectApp) {
            boolean isNeedSelectWithADF = false;
            List<ReadRecordResponse> recordResponseList;
            ApplicationSelectResponse response = trySelectWithPSE();
            if (response.isSuccess() && response.getTag88() != null) {
                // PSE success
                byte[] tag88 = StringUtil.hexStr2Bytes(response.getTag88()); // sfi
                if (tag88 != null && tag88.length > 0) {
                    recordResponseList = startReadRecord(tag88[0]);
                    if (recordResponseList.size() == 0) {
                        isNeedSelectWithADF = true;
                    }
                } else {

                }
            } else if (response.isNeedTerminate()) {
                // TODO stop emv
            } else {
                isNeedSelectWithADF = true;
            }

            if (isNeedSelectWithADF) {
                LogUtil.d(TAG, "Select with PSE -> Select with ADF");
                response = selectWithADF();
                if (response.isNeedTerminate()) {
                    // TODO stop emv
                }
            }
        }
    }

    private ApplicationSelectResponse trySelectWithPSE() {
        byte[] retData = executeApduCmd(new ApplicationSelectCmd(true, true, "1PAY.SYS.DDF01"));
        ApplicationSelectResponse response = new ApplicationSelectResponse(retData);
        if (!response.isSuccess()) {
            sendMessage(new Msg_StopEmv(Msg_StopEmv.ERR_SELECT_APP_FAILED));
            jumpToState(STATE_STOP);
        }

        return response;
    }

    private ApplicationSelectResponse selectWithADF() {

        return null;
    }

    private List<ReadRecordResponse> startReadRecord(byte sfi) {
        List<ReadRecordResponse> recordResponseList = new ArrayList<>();

        for(int recordNum = 1; recordNum < 256; recordNum++) {
            byte[] retData = executeApduCmd(new ReadRecordCmd(sfi, (byte) recordNum));
            ReadRecordResponse response = new ReadRecordResponse(retData);
            if (response.isNoRecord()) {
                break;
            }

            if (response.getTag61List() != null && response.getTag61List().size() > 0) {
                recordResponseList.add(response);
            }
        }

        return recordResponseList;
    }

    private void buildCandidateList() {

    }
}
