package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.GetProcessingOptionsCmd;
import com.vfi.android.emvkernel.corelogical.apdu.GetProcessingOptionsResponse;
import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartGPO;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.beans.DOLBean;
import com.vfi.android.emvkernel.utils.DOLUtil;
import com.vfi.android.libtools.utils.LogUtil;

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
//                    pdolData += tagMap.get()
                } else {

                }
            }
        }

        LogUtil.d(TAG, "getPDOLData return=[" + pdolData + "]");
        return pdolData;
    }
}
