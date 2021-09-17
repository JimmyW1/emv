package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.apdu.GetDataCmd;
import com.vfi.android.emvkernel.corelogical.apdu.GetDataResponse;
import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartTerminalActionAnalysis;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartTerminalRiskManagement;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.beans.TransRecord;
import com.vfi.android.emvkernel.data.beans.tagbeans.TSI;
import com.vfi.android.emvkernel.data.beans.tagbeans.TVR;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.ParamTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.Random;

public class TerminalRiskManagementState extends AbstractEmvState {
    public TerminalRiskManagementState() {
        super(EmvStateType.STATE_TERMINAL_RISK_MANAGEMENT);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        Message message = context.getMessage();
        LogUtil.d(TAG, "TerminalRiskManagementState msgType=" + message.getMessageType());
        if (message instanceof Msg_StartTerminalRiskManagement) {
            if (getEmvTransData().getAIP().isSupportTerminalRiskManagement()) {
                performTerminalRiskManagementMessage(message);
            } else {
                getEmvTransData().getTsi().markFlag(TSI.FLAG_TERMINAL_RISK_MANAGEMENT_WAS_PERFORMED, false);
                jumpToState(EmvStateType.STATE_TERMINAL_ACTION_ANALYSIS);
                sendMessage(new Msg_StartTerminalActionAnalysis());
            }
        }
    }

    private void performTerminalRiskManagementMessage(Message message) {
        LogUtil.d(TAG, "performTerminalRiskManagementMessage");

        // 1. check floor limit
        checkFloorLimit();

        // 2. Random Transaction Selection
        randomTransSelection();

        // 3. Velocity Checking
        velocityChecking();

        getEmvTransData().getTsi().markFlag(TSI.FLAG_TERMINAL_RISK_MANAGEMENT_WAS_PERFORMED, true);
        jumpToState(EmvStateType.STATE_TERMINAL_ACTION_ANALYSIS);
        sendMessage(new Msg_StartTerminalActionAnalysis());
    }

    private void checkFloorLimit() {
        LogUtil.d(TAG, "checkFloorLimit");

        String pan = "";
        String panSeqNum = "";

        long totalAmount = StringUtil.parseLong(getEmvTransData().getTagMap().get(TerminalTag.tag9F02), 0);
        LogUtil.d(TAG, "totalAmount=[" + totalAmount + "]");

        TransRecord transRecord = getEmvContext().getDbOperation().getLatestTransRecord(pan, panSeqNum, "", 1);
        if (transRecord != null) {
            totalAmount += StringUtil.parseLong(transRecord.getAmount(), 0);
            LogUtil.d(TAG, "totalAmount=[" + totalAmount + "]");
        }

        String floorLimitValue = getEmvTransData().getSelectAppTerminalParamsMap().get(ParamTag.FLOOR_LIMIT);
        LogUtil.d(TAG, "floorLimitValue=[" + floorLimitValue + "]");
        long floorLimit = StringUtil.parseLong(floorLimitValue, 0);
        LogUtil.d(TAG, "floorLimit=[" + floorLimit + "]");

        //If the sum is greater than or equal to the
        //Terminal Floor Limit, the terminal shall set the ‘Transaction exceeds floor limit’
        //bit in the TVR to 1.
        if (totalAmount >= floorLimit) {
            LogUtil.d(TAG, "Transaction exceeds floor limit");
            getEmvTransData().getTvr().markFlag(TVR.FLAG_TRANSACTION_EXCEEDS_FLOOR_LIMIT, true);
        } else {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_TRANSACTION_EXCEEDS_FLOOR_LIMIT, false);
        }
    }

    private void randomTransSelection() {
        LogUtil.d(TAG, "randomTransSelection");

        long amount = StringUtil.parseLong(getEmvTransData().getTagMap().get(TerminalTag.tag9F02), 0);
        LogUtil.d(TAG, "amount=[" + amount + "]");

        long floorLimit = StringUtil.parseLong(getEmvTransData().getSelectAppTerminalParamsMap().get(ParamTag.FLOOR_LIMIT), 0);
        LogUtil.d(TAG, "floorLimit=[" + floorLimit + "]");

        if (amount >= floorLimit) {
            LogUtil.d(TAG, "Amount >= floorLimit");
            return;
        }

        String targetPercentageValue = getEmvTransData().getSelectAppTerminalParamsMap().get(ParamTag.TARGET_PERCENTAGE);
        LogUtil.d(TAG, "targetPercentageValue=[" + targetPercentageValue + "]");
        long targetPercentage = StringUtil.parseLong(targetPercentageValue, 0);
        if (targetPercentage < 0 || targetPercentage > 99) {
            LogUtil.d(TAG, "targetPercentage should in the range of 0 to 99, force to 99");
            targetPercentage = 99;
        }

        String thresholdValue = getEmvTransData().getSelectAppTerminalParamsMap().get(ParamTag.THRESHOLD);
        LogUtil.d(TAG, "thresholdValue=[" + thresholdValue + "]");
        long threshold = StringUtil.parseLong(thresholdValue, 0);
        LogUtil.d(TAG, "threshold=[" + threshold + "]");
        if (threshold >= floorLimit) {
            LogUtil.d(TAG, "threshold >= floorLimit, force to zero");
            threshold = 0;
        }

        String maximumTargetPercentageValue = getEmvTransData().getSelectAppTerminalParamsMap().get(ParamTag.MAX_TARGET_PERCENTAGE);
        LogUtil.d(TAG, "maximumTargetPercentageValue=[" + maximumTargetPercentageValue + "]");
        long maximumTargetPercentage = StringUtil.parseLong(maximumTargetPercentageValue, 0);
        LogUtil.d(TAG, "maximumTargetPercentage=[" + maximumTargetPercentage + "]");
        if (maximumTargetPercentage < targetPercentage) {
            LogUtil.d(TAG, "maximumTargetPercentage should at least as high as the ‘Target Percentage', force to targetPercentage value");
            maximumTargetPercentage = targetPercentage;
        }

        //The terminal shall generate a random number in the range of 1 to 99
        int randomNumber = generateRandomNumber();
        if (randomNumber == 0) {
            randomNumber = 1;
        }
        LogUtil.d(TAG, "randomNumber=[" + randomNumber + "]");

        if (amount < threshold) {
            //Any transaction with a transaction amount less than the Threshold Value for
            //Biased Random Selection will be subject to selection at random without further
            //regard for the value of the transaction.

            //If this random number is less than or equal to the
            //‘Target Percentage to be used for Random Selection’, the transaction shall be
            //selected.
            if (randomNumber <= targetPercentage) {
                // Transaction selected randomly for online processing
                LogUtil.d(TAG, "Transaction selected randomly for online processing");
                getEmvTransData().getTvr().markFlag(TVR.FLAG_TRANSACTION_SELECTED_RANDOMLY_FOR_ONLINE_PROCESSING, true);
            }
        } else {
            //Any transaction with a transaction amount equal to or greater than the
            //Threshold Value for Biased Random Selection but less than the floor limit will be
            //subject to selection with bias toward sending higher value transactions online
            //more frequently (biased random selection).


            float interpolationFactor = (amount - threshold) / (floorLimit - threshold);
            long transTargetPercent = (long) ((maximumTargetPercentage - targetPercentage) * interpolationFactor + targetPercentage);
            LogUtil.d(TAG, "transTargetPercent=[" + targetPercentageValue + "]");
            if (amount <= transTargetPercent) {
                // Transaction selected randomly for online processing
                LogUtil.d(TAG, "Transaction selected randomly for online processing");
                getEmvTransData().getTvr().markFlag(TVR.FLAG_TRANSACTION_SELECTED_RANDOMLY_FOR_ONLINE_PROCESSING, true);
            }
        }
    }

    private int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(100);
    }

    private void velocityChecking() {
        LogUtil.d(TAG, "velocityChecking");

        if (!getEmvTransData().getTagMap().containsKey(EMVTag.tag9F14) || !getEmvTransData().getTagMap().containsKey(EMVTag.tag9F23)) {
            //If both the Lower Consecutive Offline Limit (tag '9F14') and Upper Consecutive
            //Offline Limit (tag '9F23') exist, the terminal shall perform velocity checking as
            //described in this section
            //If either of these data objects is not present in the ICC
            //application, the terminal shall skip this section.
            LogUtil.d(TAG, "tag9F14 or tag9F23 not exist");
            return;
        }

        String ATC = null;
        byte[] ret = executeApduCmd(new GetDataCmd(GetDataCmd.TYPE_ATC));
        GetDataResponse response = new GetDataResponse(ret);
        if (response.isSuccess()) {
            response.saveTags(getEmvTransData().getTagMap());
            ATC = response.getTlvMap().get(EMVTag.tag9F36);
        }
        LogUtil.d(TAG, "ATC=[" + ATC + "]");
        int ATCInt = StringUtil.parseInt(ATC, 16, -1);

        String lastOnlineATCRegister = null;
        ret = executeApduCmd(new GetDataCmd(GetDataCmd.TYPE_LAST_ONLINE_ATC_REGISTER));
        response = new GetDataResponse(ret);
        if (response.isSuccess()) {
            response.saveTags(getEmvTransData().getTagMap());
            lastOnlineATCRegister = response.getTlvMap().get(EMVTag.tag9F13);
        }
        LogUtil.d(TAG, "lastOnlineATCRegister=[" + lastOnlineATCRegister + "]");
        int lastOnlineATCRegisterInt = StringUtil.parseInt(lastOnlineATCRegister, 16, -1);

        if (lastOnlineATCRegisterInt == 0) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_NEW_CARD, true);
        }

        LogUtil.d(TAG, "ATCInt=[" + ATCInt + "]");
        LogUtil.d(TAG, "lastOnlineATCRegisterInt=[" + lastOnlineATCRegisterInt + "]");
        if (ATC == null || lastOnlineATCRegister == null || ATCInt <= lastOnlineATCRegisterInt) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_LOWER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED, true);
            getEmvTransData().getTvr().markFlag(TVR.FLAG_UPPER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED, true);

            return;
        }

        int lowerConsecutiveOfflineLimit = StringUtil.parseInt(getEmvTransData().getTagMap().get(EMVTag.tag9F14), 16, -1);
        int upperConsecutiveOfflineLimit = StringUtil.parseInt(getEmvTransData().getTagMap().get(EMVTag.tag9F23), 16, -1);
        LogUtil.d(TAG, "lowerConsecutiveOfflineLimit=[" + lowerConsecutiveOfflineLimit + "]");
        LogUtil.d(TAG, "upperConsecutiveOfflineLimit=[" + upperConsecutiveOfflineLimit + "]");

        if (ATCInt - lastOnlineATCRegisterInt > lowerConsecutiveOfflineLimit) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_LOWER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED, true);
        }

        if (ATCInt - lastOnlineATCRegisterInt > upperConsecutiveOfflineLimit) {
            getEmvTransData().getTvr().markFlag(TVR.FLAG_UPPER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED, true);
        }
    }
}
