package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.GetProcessingOptionsCmd;
import com.vfi.android.emvkernel.corelogical.apdu.GetProcessingOptionsResponse;
import com.vfi.android.emvkernel.corelogical.apdu.ReadRecordCmd;
import com.vfi.android.emvkernel.corelogical.apdu.ReadRecordResponse;
import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_ReSelectAppFromCandidateList;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartCardConfirm;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartGPO;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.consts.EMVResultCode;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.beans.DOLBean;
import com.vfi.android.emvkernel.data.consts.SW12;
import com.vfi.android.emvkernel.utils.DOLUtil;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

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
        LogUtil.d(TAG, "ReadCardState msgType=" + message.getMessageType());
        if (message instanceof Msg_StartGPO) {
            processStartGetProcessingOptionsMessage(message);
        }
    }

    private void processStartGetProcessingOptionsMessage(Message message) {
        // follow book 3 page 90
        getEmvTransData().clearTVR_TSI_CvmResult();
        String pdolTags = getPDOLData();
        byte[] retData = executeApduCmd(new GetProcessingOptionsCmd(pdolTags));
        GetProcessingOptionsResponse response = new GetProcessingOptionsResponse(retData);
        String offlineDataToBeAuth = "";
        getEmvTransData().setExistStaticDataRecordNotCodeWithTag70(false);
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
                    ReadRecordResponse readRecordResponse = new ReadRecordResponse(retData, false);
                    if (readRecordResponse.isSuccess()) {
                        if (numOfOfflineDataRecords > 0 && recordNum < startRecordNum + numOfOfflineDataRecords) {
                            LogUtil.d(TAG, "save offline data authentication sfi=[" + sfi + "] recordNum=[" + recordNum + "]");
                            if (readRecordResponse.getTag70() == null || readRecordResponse.getTag70().length() == 0) {
                                getEmvTransData().setExistStaticDataRecordNotCodeWithTag70(true);
                            }
                            if (recordNum >= 1 && recordNum <= 10) {
                                offlineDataToBeAuth += readRecordResponse.getTag70();
                            } else {
                                offlineDataToBeAuth += StringUtil.byte2HexStr(readRecordResponse.getData());
                            }
                        }
                        readRecordResponse.saveTags(getEmvTransData().getTagMap());
                    } else {
                        LogUtil.d(TAG, "Error: Read record error[" + readRecordResponse.getStatus() + "] recordNum=[" + recordNum + "]");
                        setErrorCode(EMVResultCode.ERR_READ_RECORD_FAILED);
                        stopEmv();
                        return;
                    }
                }
            }

            if (checkIfMissingMandatoryData()) {
                setErrorCode(EMVResultCode.ERR_MISSING_MANDATORY_DATA);
                stopEmv();
            } else {
                LogUtil.d(TAG, "offlineDataToBeAuth=[" + offlineDataToBeAuth + "]");
                getEmvTransData().setStaticDataToBeAuthenticated(offlineDataToBeAuth);
                // TODO
                jumpToState(EmvStateType.STATE_CARD_CONFIRM);
                sendMessage(new Msg_StartCardConfirm());
            }
        } else if (SW12.ERR_CONDITION_NOT_SATISFIED.equals(response.getStatus())
                && getEmvTransData().getCandidateList().size() > 1) {
            LogUtil.d(TAG, "Error: GPO condition not satisfied, back to select Application again");
            removeCandidateApplication(getEmvTransData().getTagMap().get(EMVTag.tag84));
            jumpToState(EmvStateType.STATE_SELECT_APP);
            sendMessage(new Msg_ReSelectAppFromCandidateList());
        } else {
            LogUtil.d(TAG, "Error: GPO failed[" + response.getStatus() + "]");
            setErrorCode(EMVResultCode.ERR_GPO_FAILED);
            stopEmv();
        }
    }

    /**
     * When any mandatory data object is missing, the terminal terminates the
     * transaction unless otherwise specified in these specifications.
     */
    private boolean checkIfMissingMandatoryData() {
        boolean isMissingMandatoryData = false;

        Map<String, String> tagMap = getEmvTransData().getTagMap();
        if (!tagMap.containsKey(EMVTag.tag5F24)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 5F24 (Expiration Date)");
            isMissingMandatoryData = true;
        }

        if (!tagMap.containsKey(EMVTag.tag5A)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 5A (PAN)");
            isMissingMandatoryData = true;
        }

        if (!tagMap.containsKey(EMVTag.tag8C)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 8C (CDOL1)");
            isMissingMandatoryData = true;
        }

        if (!tagMap.containsKey(EMVTag.tag8D)) {
            LogUtil.d(TAG, "Error: Missing Mandatory tag 8D (CDOL2)");
            isMissingMandatoryData = true;
        }

        //If the terminal encounters formatting errors in the CVM List such as a list with
        //an odd number of bytes (that is, with an incomplete CVM Rule), the terminal
        //shall terminate the transaction as specified in Book 3 section 7.5.
        if (tagMap.containsKey(EMVTag.tag8E)) {
            if (tagMap.get(EMVTag.tag8E).length() % 2 != 0) {
                LogUtil.d(TAG, "Wrong tag 8E, cvm list with an odd number of bytes.");
                isMissingMandatoryData = true;
            }
        }

        LogUtil.d(TAG, "isMissingMandatoryData=[" + isMissingMandatoryData + "]");
        return isMissingMandatoryData;
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
                    pdolData += StringUtil.getNonNullStringLeftPadding("0", dolBean.getLen() * 2);
                }
            }
        }

        LogUtil.d(TAG, "getPDOLData return=[" + pdolData + "]");
        return pdolData;
    }
}
