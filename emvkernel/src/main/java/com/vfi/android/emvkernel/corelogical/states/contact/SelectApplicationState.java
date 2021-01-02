package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectCmd;
import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectResponse;
import com.vfi.android.emvkernel.corelogical.apdu.ReadRecordCmd;
import com.vfi.android.emvkernel.corelogical.apdu.ReadRecordResponse;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartSelectApp;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StopEmv;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.data.beans.EmvApplication;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;
import com.vfi.android.libtools.utils.TLVUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                    if (recordResponseList.size() <= 0) {
                        isNeedSelectWithADF = true;
                    } else {
                        buildCandidateList(recordResponseList);
                        if (getEmvTransData().getCandidateList().size() <= 0) {
                            isNeedSelectWithADF = true;
                        } else if (getEmvTransData().getCandidateList().size() == 1){
                            // TODO
                        } else {
                            // TODO
                        }
                    }
                } else {
                    isNeedSelectWithADF = true;
                }
            } else if (response.isNeedTerminate()) {
                // TODO stop emv
            } else {
                isNeedSelectWithADF = true;
            }

            if (isNeedSelectWithADF) {
                LogUtil.d(TAG, "Select with PSE -> Select with ADF");
                selectWithADFAndBuildCandidateList();
                if (getEmvTransData().getCandidateList().size() <= 0) {
                    // TODO
                } else if (getEmvTransData().getCandidateList().size() == 1){
                    // TODO
                } else {
                    // TODO
                }
            }
        }
    }

    private ApplicationSelectResponse trySelectWithPSE() {
        byte[] retData = executeApduCmd(new ApplicationSelectCmd(true, "1PAY.SYS.DDF01"));
        ApplicationSelectResponse response = new ApplicationSelectResponse(retData);
        return response;
    }

    private void selectWithADFAndBuildCandidateList() {
        clearCandidateList();

        List<Map<String, String>> terminalApplicationMapList = getEmvTransData().getTerminalApplicationMapList();
        for (Map<String, String> map : terminalApplicationMapList) {
            String appName = map.get(TerminalTag.AID);
            String asi = map.get(TerminalTag.ASI);
            boolean isFullMatch = (asi == null || !asi.equals("00"));
            ApplicationSelectResponse response = selectWithADF(true, appName, isFullMatch);
            if (response.isNeedTerminate()) {
                // TODO
                break;
            }
        }
    }

    private ApplicationSelectResponse selectWithADF(boolean isSelectFirst, String appName, boolean isFullMatch) {
        byte[] retData = executeApduCmd(new ApplicationSelectCmd(isSelectFirst, appName));
        ApplicationSelectResponse response = new ApplicationSelectResponse(retData);
        if (response.isSuccess() || response.isApplicationBlocked()) {
            String dfName = response.getTag84();

            if (dfName.startsWith(appName)) {
                if (!response.isApplicationBlocked()) {
                    addCandidateApplication(new EmvApplication(dfName));
                }

                if (!isFullMatch && appName.length() != dfName.length()){
                    return selectWithADF(false, appName, false);
                }
            } else {
                // no happened.
            }
        } else {
            LogUtil.d(TAG, "Select with AID[" + appName + "] not found.");
        }

        return response;
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

    private void buildCandidateList(List<ReadRecordResponse> readRecordResponseList) {
        LogUtil.d(TAG, "buildCandidateList");
        List<Map<String, String>> terminalApplicationMapList = getEmvTransData().getTerminalApplicationMapList();

        for (ReadRecordResponse response : readRecordResponseList) {
            for (String tag61 : response.getTag61List()) {
                Map<String, String> tag61Map = TLVUtil.toTlvMap(tag61);

                for (Map<String, String> map : terminalApplicationMapList) {
                    if (tag61Map.containsKey(EMVTag.tag4F) && map.containsKey(TerminalTag.AID)) {
                        String asi = map.get(TerminalTag.ASI);
                        boolean isFullMatch = (asi == null || !asi.equals("00"));
                        String tag4F = tag61Map.get(EMVTag.tag4F);
                        String terminalAid = map.get(TerminalTag.AID);
                        LogUtil.d(TAG, "==> Tag4F[" + tag4F + "] Terminal AID[" + terminalAid + "] isFullMatch=" + isFullMatch);

                        if ((isFullMatch && tag4F.equals(terminalAid)) || (!isFullMatch && tag4F.startsWith(terminalAid))) {
                            EmvApplication emvApplication = new EmvApplication(tag61Map.get(EMVTag.tag4F));
                            addCandidateApplication(emvApplication);
                        }
                    }
                }
            }
        }
    }
}
