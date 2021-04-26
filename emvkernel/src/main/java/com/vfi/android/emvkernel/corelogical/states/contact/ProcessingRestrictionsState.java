package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.msgs.base.Message;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartCardHolderVerification;
import com.vfi.android.emvkernel.corelogical.msgs.emvmsgs.Msg_StartProcessingRestrictions;
import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvContext;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;
import com.vfi.android.emvkernel.data.beans.tagbeans.AUC;
import com.vfi.android.emvkernel.data.beans.tagbeans.TVR;
import com.vfi.android.emvkernel.data.consts.EMVTag;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

public class ProcessingRestrictionsState extends AbstractEmvState {
    public ProcessingRestrictionsState() {
        super(EmvStateType.STATE_PROCESSING_RESTRICTIONS);
    }

    @Override
    public void run(EmvContext context) {
        super.run(context);

        Message message = context.getMessage();
        LogUtil.d(TAG, "ProcessingRestrictionsState msgType=" + message.getMessageType());

        if (message instanceof Msg_StartProcessingRestrictions) {
            processRestrictions();
        }
    }

    private void processRestrictions() {
        // 1. check Application Version Number
        checkApplicationVersionNumber();

        // 2. check Application Usage Control
        checkApplicationUsageControl();

        // 3. Application Effective/Expiration Dates Checking
        checkApplicationEffectiveAndExpirationDates();

        jumpToState(EmvStateType.STATE_CARDHOLDER_VERIFICATION);
        sendMessage(new Msg_StartCardHolderVerification());
    }

    private void checkApplicationVersionNumber() {
        LogUtil.d(TAG, "checkApplicationVersionNumber");

        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag9F08)) {
            String iccAppVersionNum = getEmvTransData().getTagMap().get(EMVTag.tag9F08);
            String terminalAppVersionNum = getEmvTransData().getTagMap().get(TerminalTag.tag9F09);
            LogUtil.d(TAG, "iccAppVersionNum=[" + iccAppVersionNum + "] terminalAppVersionNum=[" + terminalAppVersionNum + "]");
            if (!iccAppVersionNum.equals(terminalAppVersionNum)) {
                LogUtil.d(TAG, "ICC and terminal have different app version.");
                getEmvTransData().getTvr().markFlag(TVR.FLAG_ICC_TERMINAL_HAVE_DIFFERENT_APP_VERSION, true);
            }
        } else {
            //If the Application Version Number is not present in the ICC, the terminal shall presume the
            //terminal and ICC application versions are compatible, and transaction
            //processing shall continue.
            LogUtil.d(TAG, "ICC application version number not found.");
        }
    }

    private void checkApplicationUsageControl() {
        LogUtil.d(TAG, "checkApplicationUsageControl");

        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag9F07)) {
            AUC auc = new AUC(getEmvTransData().getTagMap().get(EMVTag.tag9F07));

            // 1. If the transaction is not being conducted at an ATM, the ‘Valid at terminals
            //other than ATMs’ bit must be on in Application Usage Control.
            if (!auc.isBitSetOn(AUC.FLAG_VALID_AT_TERMINALS_OTHER_THAN_ATMS)) {
                LogUtil.d(TAG, "Valid at terminals other than ATMs failed.");
                getEmvTransData().getTvr().markFlag(TVR.FLAG_REQUEST_SERVICE_NOT_ALLOW_FOR_CARD_PRODUCT, true);
                return;
            }

            String terminalCountryCode = getEmvTransData().getTagMap().get(TerminalTag.tag9F1A);
            LogUtil.d(TAG, "terminalCountryCode=[" + terminalCountryCode + "]");

            if (getEmvTransData().getTagMap().containsKey(EMVTag.tag5F28) &&
                    terminalCountryCode != null && terminalCountryCode.length() > 0) {
                String issuerCountryCode = getEmvTransData().getTagMap().get(EMVTag.tag5F28);
                LogUtil.d(TAG, "issuerCountryCode=[" + issuerCountryCode + "]");

                String transactionType = getEmvTransData().getTagMap().get(TerminalTag.tag9C);
                if (!auc.isCurrentTransCorrect(transactionType, issuerCountryCode.equals(terminalCountryCode))) {
                    LogUtil.d(TAG, "isCurrentTransCorrect false");
                    getEmvTransData().getTvr().markFlag(TVR.FLAG_REQUEST_SERVICE_NOT_ALLOW_FOR_CARD_PRODUCT, true);
                    return;
                }
            } else {
                // If this data object is present, the terminal shall make the following checks
                LogUtil.d(TAG, "Icc or terminal country code not found.");
            }
        } else {
            // If this data object is present, the terminal shall make the following checks
            LogUtil.d(TAG, "ICC Application Usage Control not found.");
        }
    }

    private void checkApplicationEffectiveAndExpirationDates() {
        LogUtil.d(TAG, "checkApplicationEffectiveAndExpirationDates");

        String currentYYYYMMDD = StringUtil.getSystemDate();

        if (getEmvTransData().getTagMap().containsKey(EMVTag.tag5F25)) {
            String  applicationEffectiveDate = getEmvTransData().getTagMap().get(EMVTag.tag5F25);
            LogUtil.d(TAG, "applicationEffectiveDate=[" + applicationEffectiveDate + "]");
            String year = applicationEffectiveDate.substring(0, 2);
            if (StringUtil.parseInt(year, 0) <= 50) {
                year = "20" + year;
            } else {
                year = "19" + year;
            }
            String applicationEffectiveDateYYYYMMDD = year + applicationEffectiveDate.substring(2);
            LogUtil.d(TAG, "currentYYYYMMDD=[" + currentYYYYMMDD + "] applicationEffectiveDateYYYYMMDD=[" + applicationEffectiveDateYYYYMMDD + "]");
            if (StringUtil.parseInt(currentYYYYMMDD, 0) < StringUtil.parseInt(applicationEffectiveDateYYYYMMDD, 0)) {
                LogUtil.d(TAG, "Application effective date expired");
                getEmvTransData().getTvr().markFlag(TVR.FLAG_APPLICATION_NOT_YET_EFFECTIVE, true);
            }
        }

        String applicationExpirationDate = getEmvTransData().getTagMap().get(EMVTag.tag5F24);
        LogUtil.d(TAG, "applicationExpirationDate=[" + applicationExpirationDate + "]");
        String year = applicationExpirationDate.substring(0, 2);
        if (StringUtil.parseInt(year, 0) <= 50) {
            year = "20" + year;
        } else {
            year = "19" + year;
        }
        String applicationExpirationDateYYYYMMDD = year + applicationExpirationDate.substring(2);
        LogUtil.d(TAG, "currentYYYYMMDD=[" + currentYYYYMMDD + "] applicationEffectiveDateYYYYMMDD=[" + applicationExpirationDateYYYYMMDD + "]");
        if (StringUtil.parseInt(currentYYYYMMDD, 0) > StringUtil.parseInt(applicationExpirationDateYYYYMMDD, 0)) {
            LogUtil.d(TAG, "Application expiration date expired");
            getEmvTransData().getTvr().markFlag(TVR.FLAG_EXPIRED_APPLICATION, true);
        }
    }
}
