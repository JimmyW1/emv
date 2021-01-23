package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.GetProcessingOptionsCmd;
import com.vfi.android.emvkernel.corelogical.apdu.GetProcessingOptionsResponse;
import com.vfi.android.emvkernel.corelogical.apdu.ReadRecordCmd;
import com.vfi.android.emvkernel.corelogical.apdu.ReadRecordResponse;
import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartGPO;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.beans.DOLBean;
import com.vfi.android.emvkernel.utils.DOLUtil;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadCardState extends AbstractEmvState {
    public ReadCardState() {
        super(EmvStateType.STATE_READ_CARD);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        Message message = context.getMessage();
        if (message instanceof Msg_StartGPO) {
            processStartGetProcessingOptionsMessage(message);
        }
    }

    private void processStartGetProcessingOptionsMessage(Message message) {
        // follow book 3 page 90
        getEmvTransData().clearTVRAndTSI();
        String pdolTags = getPDOLData();
        byte[] retData = executeApduCmd(new GetProcessingOptionsCmd(pdolTags));
        GetProcessingOptionsResponse response = new GetProcessingOptionsResponse(retData);
        List<ReadRecordResponse> offlineDataAuthRecordList = new ArrayList<>();
        if (response.isSuccess()) {
            response.saveTags(getEmvTransData().getTagMap());
            String AFL = response.getAFL();
            byte[] AFLBytes = StringUtil.hexStr2Bytes(AFL);
            for (int i = 0; i < AFLBytes.length; i += 4) {
                byte sfi = (byte) (AFLBytes[i+0] >> 3);
                byte startRecordNum = AFLBytes[i+1];
                byte endRecordNum = AFLBytes[i+2];
                int numOfOfflineDataRecords = AFLBytes[i+3];
                LogUtil.d(TAG, "sfi=[" + sfi + "] startRecordNum=[" + startRecordNum + "] endRecordNum=[" + endRecordNum + "] numOfOfflineDataRecords=[" + numOfOfflineDataRecords + "]");

                for (int recordNum = startRecordNum; recordNum <= endRecordNum; recordNum++) {
                    retData = executeApduCmd(new ReadRecordCmd(sfi, (byte) recordNum));
                    ReadRecordResponse readRecordResponse = new ReadRecordResponse(retData);
                    if (numOfOfflineDataRecords > 0 && recordNum < startRecordNum + numOfOfflineDataRecords) {
                        LogUtil.d(TAG, "save offline data authentication sfi=[" + sfi + "] recordNum=[" + recordNum + "]");
                        offlineDataAuthRecordList.add(readRecordResponse);
                    } else {
                        readRecordResponse.saveTags(getEmvTransData().getTagMap());
                    }
                }
            }
        } else {
            stopEmv();
        }
    }

    private String getPDOLData() {
        String pdolTags = "";
        Map<String, String> tagMap = getEmvTransData().getTagMap();
        if (tagMap.containsKey(EMVTag.tag9F38)) {
            pdolTags = tagMap.get(EMVTag.tag9F38);
        }

        String pdolData = "";
        LogUtil.d(TAG, "pdolTags=[" + pdolTags + "]");
        if (pdolTags != null && pdolTags.length() > 0) {
            List<DOLBean> dolBeanList = DOLUtil.toDOLDataList(pdolTags);
            for (DOLBean dolBean : dolBeanList) {
                if (tagMap.containsKey(dolBean.getTag())) {
                    pdolData += dolBean.formatValue(tagMap.get(dolBean.getTag()));
                } else {
                    pdolData += StringUtil.getNonNullStringLeftPadding("0", dolBean.getLen());
                }
            }
        }

        LogUtil.d(TAG, "getPDOLData return=[" + pdolData + "]");
        return pdolData;
    }
}
