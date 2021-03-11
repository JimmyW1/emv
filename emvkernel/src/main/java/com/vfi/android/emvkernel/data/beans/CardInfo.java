package com.vfi.android.emvkernel.data.beans;

import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;

public class CardInfo {
    private String TAG = TAGS.EMV_STATE;

    private String pan;
    private String track1;
    private String track2;
    private String track3; // No track3 for IC card
    private String serviceCode; // get from track 2
    private String expiredDate; // get from track 2
    private String cardSequenceNum;
    /**
     * will according to EmvParams.cardConfirmTagList to return.
     */
    private String tags;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        LogUtil.d(TAG, "pan=[" + pan + "]");
        this.pan = pan;
    }

    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        LogUtil.d(TAG, "track1=[" + track1 + "]");
        this.track1 = track1;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        LogUtil.d(TAG, "track2=[" + track2 + "]");
        this.track2 = track2;
        if (track2 != null && (track2.contains("D"))) {
            String subString = track2.substring(track2.indexOf("D") + 1);
            serviceCode = subString.substring(0, 4);
            expiredDate = subString.substring(4, 7);
            LogUtil.d(TAG, "serviceCode=[" + serviceCode + "]");
            LogUtil.d(TAG, "expiredDate=[" + expiredDate + "]");
        }
    }

    public String getTrack3() {
        return track3;
    }

    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public String getCardSequenceNum() {
        return cardSequenceNum;
    }

    public void setCardSequenceNum(String cardSequenceNum) {
        LogUtil.d(TAG, "cardSequenceNum=[" + cardSequenceNum + "]");
        this.cardSequenceNum = cardSequenceNum;
    }

    @Override
    public String toString() {
        return "CardInfo{" +
                "TAG='" + TAG + '\'' +
                ", pan='" + pan + '\'' +
                ", track1='" + track1 + '\'' +
                ", track2='" + track2 + '\'' +
                ", track3='" + track3 + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", expiredDate='" + expiredDate + '\'' +
                ", cardSequenceNum='" + cardSequenceNum + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
