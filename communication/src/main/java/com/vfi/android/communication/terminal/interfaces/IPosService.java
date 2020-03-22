package com.vfi.android.communication.terminal.interfaces;

import com.vfi.android.communication.terminal.beans.CardInformation;
import com.vfi.android.communication.terminal.beans.CheckCardParamIn;

import io.reactivex.Observable;


public interface IPosService {
    Observable<Boolean> bind();

    // check card related interfaces
    Observable<CardInformation> startCheckCard(CheckCardParamIn checkCardParamIn);

    Observable<Object> stopCheckCard();

    Observable<Boolean> isIcCardPresent();

    Observable<Boolean> checkIsCardRemoved(int checkTimeSecond);

    Observable<Boolean> simulatorCardRemoved();

    Observable<Boolean> isRfCardPresent();

    Observable<Boolean> powerOnSmartCardReader();

    Observable<Boolean> powerOffSmartCardReader();

    Observable<byte[]> executeAPDU(byte[] apduCmd);
}
