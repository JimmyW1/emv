package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectCmd;
import com.vfi.android.emvkernel.corelogical.apdu.ApplicationSelectResponse;
import com.vfi.android.emvkernel.corelogical.apdu.ReadRecordCmd;
import com.vfi.android.emvkernel.corelogical.apdu.ReadRecordResponse;
import com.vfi.android.emvkernel.corelogical.msgs.appmsgs.Msg_CardHolderSelectFinished;
import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartSelectApp;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StopEmv;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.data.beans.AppInfo;
import com.vfi.android.emvkernel.data.beans.EmvApplication;
import com.vfi.android.emvkernel.data.consts.EMVResultCode;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;
import com.vfi.android.libtools.utils.TLVUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        Message message = context.getMessage();
        LogUtil.d(TAG, "SelectApplicationState msgType=" + message.getMessageType());
        if (message instanceof Msg_StartSelectApp) {
            processStartSelectAppMessage(message);
        } else if (message instanceof Msg_CardHolderSelectFinished) {
            processCardHolderSelectFinishedMessage(message);
        }
    }

    private void processStartSelectAppMessage(Message message) {
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
                    } else {
                        selectCandidateApplication();
                    }
                }
            } else {
                isNeedSelectWithADF = true;
            }
        } else if (response.isNeedTerminate()) {
            setErrorCode(response.getErrorCode());
            stopEmv();
        } else {
            isNeedSelectWithADF = true;
        }

        if (isNeedSelectWithADF) {
            LogUtil.d(TAG, "Select with PSE -> Select with ADF");
            selectWithADFAndBuildCandidateList();
            selectCandidateApplication();
        }
    }

    private void processCardHolderSelectFinishedMessage(Message message) {
        Msg_CardHolderSelectFinished msg = (Msg_CardHolderSelectFinished) message;
        LogUtil.d(TAG, "isCancelled=" + msg.isCancelled() + " selected DF name=[" + msg.getSelectedDfName() + "]");
        if (msg.isCancelled()) {
            setErrorCode(EMVResultCode.ERR_CARDHOLDER_CANCELLED_TRANS);
            stopEmv();
        } else {
            doFinalSelectProcess(msg.getSelectedDfName());
        }
    }

    private void selectCandidateApplication() {
        List<EmvApplication> candidateAppList = getEmvTransData().getCandidateList();
        if (candidateAppList.size() == 0) {
            setErrorCode(EMVResultCode.ERR_EMPTY_CANDIDATE_LIST);
            stopEmv();
        } else if (candidateAppList.size() == 1){
            EmvApplication emvApplication = getEmvTransData().getCandidateList().get(0);
            if (emvApplication.isAutoSelect()) {
                doFinalSelectProcess(emvApplication.getDfName());
            } else if (!getEmvContext().getEmvParams().isSupportCardHolderSelect()){
                setErrorCode(EMVResultCode.ERR_NOT_SUPPORT_CARDHOLDER_SELECT);
                stopEmv();
            }
        } else if (getEmvContext().getEmvParams().isSupportCardHolderSelect()){
            if (getEmvHandler() != null) {
                getEmvHandler().onSelectApplication(getOrderedEmvApplications());
            } else {
                setErrorCode(EMVResultCode.ERR_NOT_SET_EMV_HANDLER);
                stopEmv();
            }
        } else {
            autoSelectEmvApplication();
        }
    }

    private List<AppInfo> getOrderedEmvApplications() {
        List<AppInfo> appInfoList = new ArrayList<>();

        for (EmvApplication emvApplication : getEmvTransData().getCandidateList()) {
            AppInfo appInfo = new AppInfo(emvApplication.getLabel(), emvApplication.getDfName(), emvApplication.getAppPriorityIndicator());
            appInfoList.add(appInfo);
        }

        Collections.sort(appInfoList, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo app1, AppInfo app2) {
                return app1.getAppPriorityIndicator() - app2.getAppPriorityIndicator();
            }
        });

        return appInfoList;
    }

    private void autoSelectEmvApplication() {
        List<EmvApplication> emvApplicationList = getEmvTransData().getCandidateList();
        String highestPriorityDfName = null;
        byte highestPriority = '0';

        for (EmvApplication emvApplication : emvApplicationList) {
            if (!emvApplication.isAutoSelect()) {
                continue;
            } else if (highestPriorityDfName == null || emvApplication.getAppPriorityIndicator() < highestPriority){
                highestPriorityDfName = emvApplication.getDfName();
                highestPriority = emvApplication.getAppPriorityIndicator();
            }
        }

        LogUtil.d(TAG, "autoSelectEmvApplication dfName=[" + highestPriorityDfName + "]");
        if (highestPriorityDfName == null) {
            setErrorCode(EMVResultCode.ERR_NOT_SUPPORT_CARDHOLDER_SELECT);
            stopEmv();
        } else {
            doFinalSelectProcess(highestPriorityDfName);
        }
    }

    private void doFinalSelectProcess(String dfName) {
        byte[] retData = executeApduCmd(new ApplicationSelectCmd(true, dfName));
        ApplicationSelectResponse response = new ApplicationSelectResponse(retData);
        if (response.isSuccess()) {
            // TODO set 9F06 equal to tag84
        } else {
            removeCandidateApplication(dfName);
            selectCandidateApplication(); // select again
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
                setErrorCode(response.getErrorCode());
                stopEmv();
                break;
            }
        }
    }

    private ApplicationSelectResponse selectWithADF(boolean isSelectFirst, String appName, boolean isFullMatch) {
        byte[] retData = executeApduCmd(new ApplicationSelectCmd(isSelectFirst, appName));
        ApplicationSelectResponse response = new ApplicationSelectResponse(retData);
        if (response.isSuccess() || response.isApplicationBlocked()) {
            String dfName = response.getTag84();
            String applicationLabel = getApplicationLabel(response.getTag50());

            if (dfName.startsWith(appName)) {
                if (!response.isApplicationBlocked()) {
                    if (applicationLabel != null && applicationLabel.length() > 0) {
                        addCandidateApplication(new EmvApplication(dfName, applicationLabel, getAppPriorityIndicator(response.getTag87())));
                    }
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
                            if (tag61Map.containsKey(EMVTag.tag50)) {
                                byte appPriorityIndicator = getAppPriorityIndicator(tag61Map.get(EMVTag.tag87));
                                String applicationLabel = getApplicationLabel(tag61Map.get(EMVTag.tag50));
                                if (applicationLabel != null && applicationLabel.length() > 0) {
                                    EmvApplication emvApplication = new EmvApplication(tag4F, applicationLabel, appPriorityIndicator);
                                    addCandidateApplication(emvApplication);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public byte getAppPriorityIndicator(String tag87) {
        byte appPriorityIndicator = '0';

        if (tag87 != null && tag87.length() == 2) {
            appPriorityIndicator = StringUtil.hexStr2Bytes(tag87)[0];
        }

        return appPriorityIndicator;
    }

    public String getApplicationLabel(String tag50) {
        // TODO
        return tag50;
    }
}
